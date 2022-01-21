/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 *     This file is part of Menza.
 *
 *     Menza is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Menza is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Menza.  If not, see <https://www.gnu.org/licenses/>.
 */

package cz.lastaapps.storage.repo

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.ContactsScraper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

class ContactsRepoImpl<R : Any>(
    database: MenzaDatabase,
    private val scraper: ContactsScraper<R>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ContactsRepo {

    companion object {
        private val log = logging(ContactsRepo::class.simpleName)
    }

    private val queries = database.contactQueries
    override fun getContactsForMenza(menzaId: MenzaId): Flow<List<Contact>> {
        return queries.getContactById(menzaId) { newMenzaId, name, role, phone, email ->
            Contact(newMenzaId, name, role, phone, email)
        }.asFlow().mapToList(dispatcher)
    }

    override val errors: Channel<Errors>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<Errors>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    override fun getData(scope: CoroutineScope): Flow<List<Contact>> {

        log.i { "Getting data" }
        scope.launch(dispatcher) {
            val hasData = hasData()
            if (!hasData) {
                refreshInternal()
            }
        }

        return queries.getAll { menzaId, name, role, phone, email ->
            Contact(menzaId, name, role, phone, email)
        }
            .asFlow().mapToList(scope.coroutineContext)
    }

    override fun refreshData(): Flow<Boolean?> {
        return flow {
            log.i { "Requesting data refresh" }
            emit(refreshInternal())
        }.flowOn(dispatcher)
    }

    private suspend fun refreshInternal(): Boolean? {
        if (mRequestInProgress.value)
            return null

        mRequestInProgress.value = true

        val request = try {
            log.i { "Getting data from a server" }
            scraper.createRequest()
        } catch (e: Exception) {
            mErrors.send(Errors.ConnectionError)
            e.printStackTrace()
            mRequestInProgress.value = false
            return false
        }
        val data = try {
            log.i { "Scraping" }
            scraper.scrape(request)
        } catch (e: Exception) {
            mErrors.send(Errors.ParsingError)
            e.printStackTrace()
            mRequestInProgress.value = false
            return false
        }

        log.i { "Replacing database entries" }
        queries.transaction {
            queries.delete()
            data.forEach {
                queries.insert(it.id, it.name, it.role, it.phoneNumber, it.email)
            }
        }
        mRequestInProgress.value = true
        return true
    }

    override suspend fun hasData(): Boolean =
        hasDataStored().first().also { log.i { "hasData: $it" } }

    override fun hasDataStored(): Flow<Boolean> {
        log.i { "Asking hasData" }
        return queries.rowNumber().asFlow().mapToOneNotNull(dispatcher)
            .map { it > 0 }
    }
}