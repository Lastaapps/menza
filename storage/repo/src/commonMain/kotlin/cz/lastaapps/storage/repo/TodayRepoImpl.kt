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

import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.scraping.TodayScraper
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class TodayRepoImpl(
    private val scraper: TodayScraper,
    private val menzaId: MenzaId,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TodayRepo {

    companion object {
        private val log = logging(TodayRepo::class.simpleName)
    }

    override val errors: Channel<MenzaError>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<MenzaError>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    override suspend fun getData(): Set<Dish>? =
        withContext(dispatcher) {
            if (mRequestInProgress.value)
                return@withContext null

            mRequestInProgress.value = true

            val request = try {
                log.i { "Getting data from a server for $menzaId" }
                scraper.createRequest(menzaId).bodyAsText()
            } catch (e: Exception) {
                log.e(e) { "Download failed" }
                mErrors.send(e.toMenzaError())
                return@withContext null
            }
            val data = try {
                log.i { "Scraping $menzaId" }
                scraper.scrape(request)
            } catch (e: Exception) {
                mErrors.send(MenzaError.ParsingError(e))
                log.e(e) { "Parsing error" }
                e.printStackTrace()
                return@withContext null
            }

            return@withContext data
        }.also { mRequestInProgress.value = false }
}