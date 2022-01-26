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
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.Message
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.MessagesScraper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.lighthousegames.logging.logging

class MessagesRepoImpl<R : Any>(
    database: MenzaDatabase,
    private val scraper: MessagesScraper<R>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : MessagesRepo {

    companion object {
        private val log = logging(MessagesRepo::class.simpleName)
    }

    private val queries = database.messageQueries
    override fun getMessage(menzaId: MenzaId): Flow<List<Message>> {
        return queries.getMessageForMenza(menzaId) { id, name ->
            Message(id, name)
        }.asFlow().mapToList(dispatcher)
    }

    override val errors: Channel<MenzaError>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<MenzaError>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    override fun getData(scope: CoroutineScope): Flow<List<Message>> {

        log.i { "Getting data" }
        scope.launch(dispatcher) {
            val hasData = hasData()
            if (!hasData) {
                refreshInternal()
            }
        }

        return queries.getAll { menzaId, message -> Message(menzaId, message) }
            .asFlow().mapToList(dispatcher)
    }

    override fun refreshData(): Flow<Boolean?> {
        return flow {
            log.i { "Requesting data refresh" }
            emit(refreshInternal())
        }.flowOn(dispatcher)
    }

    private suspend fun refreshInternal(): Boolean? = withContext(dispatcher) {
        if (mRequestInProgress.value)
            return@withContext null

        mRequestInProgress.value = true

        val request = try {
            log.i { "Getting data from a server" }
            scraper.createRequest()
        } catch (e: Exception) {
            log.e(e) { "Download failed" }
            mErrors.send(e.toMenzaError())
            return@withContext false
        }
        val data = try {
            log.i { "Scraping" }
            scraper.scrape(request)
        } catch (e: Exception) {
            mErrors.send(MenzaError.ParsingError(e))
            log.e(e) { "Parsing error" }
            e.printStackTrace()
            return@withContext false
        }

        log.i { "Replacing database entries" }
        queries.transaction {
            queries.delete()
            data.forEach {
                queries.insert(it.id, it.message)
            }
        }
        return@withContext true
    }.also { mRequestInProgress.value = false }

    override suspend fun hasData(): Boolean =
        hasDataStored().first().also { log.i { "hasData: $it" } }

    override fun hasDataStored(): Flow<Boolean> {
        log.i { "Asking hasData" }
        return queries.rowNumber().asFlow().mapToOneNotNull(dispatcher)
            .map { it > 0 }
    }

    override suspend fun clearData() {
        queries.delete()
    }
}