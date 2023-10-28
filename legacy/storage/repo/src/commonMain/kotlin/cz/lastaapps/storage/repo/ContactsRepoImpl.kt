/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

import app.cash.sqldelight.runtime.coroutines.asFlow
import app.cash.sqldelight.runtime.coroutines.mapToList
import app.cash.sqldelight.runtime.coroutines.mapToOneNotNull
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.ContactsScraper
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class ContactsRepoImpl(
    database: MenzaDatabase,
    private val scraper: ContactsScraper,
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

    override val errors: Channel<MenzaScrapingError>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<MenzaScrapingError>(Channel.BUFFERED)
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

    private suspend fun refreshInternal(): Boolean? = withContext(dispatcher) {
        if (mRequestInProgress.value) {
            return@withContext null
        }

        mRequestInProgress.value = true

        val request = try {
            log.i { "Getting data from a server" }
            scraper.createRequest().bodyAsText()
        } catch (e: Exception) {
            log.e(e) { "Download failed" }
            mErrors.send(e.toMenzaError())
            return@withContext false
        }
        val data = try {
            log.i { "Scraping" }
            scraper.scrape(request)
        } catch (e: Exception) {
            mErrors.send(MenzaScrapingError.ParsingError(e))
            log.e(e) { "Parsing error" }
            e.printStackTrace()
            return@withContext false
        }

        log.i { "Replacing database entries" }
        queries.transaction {
            queries.delete()
            data.forEach {
                queries.insert(it.id, it.name, it.role, it.phoneNumber, it.email)
            }
        }

        return@withContext true
    }.also { mRequestInProgress.value = false }

    override suspend fun hasData(): Boolean =
        true // TODO revert
    // hasDataStored().first().also { log.i { "hasData: $it" } }

    override fun hasDataStored(): Flow<Boolean> {
        log.i { "Asking hasData" }
        return queries.rowNumber().asFlow().mapToOneNotNull(dispatcher)
            .map { it > 0 }
    }

    override suspend fun clearData() {
        queries.delete()
    }
}
