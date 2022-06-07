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

import cz.lastaapps.entity.exceptions.WeekNotAvailable
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.entity.week.WeekNumber
import cz.lastaapps.scraping.WeekScraper
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class WeekRepoImpl(
    private val scraper: WeekScraper,
    private val menzaId: MenzaId,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : WeekRepo {

    companion object {
        private val log = logging(WeekRepo::class.simpleName)
    }

    override val errors: Channel<MenzaError>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<MenzaError>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    override suspend fun getData(): Set<WeekDish>? =
        withContext(dispatcher) {
            if (mRequestInProgress.value)
                return@withContext null

            mRequestInProgress.value = true

            log.i { "Getting data from a server for $menzaId" }
            val request = try {
                scraper.createRequest(menzaId, WeekNumber.tempWeekNumber).bodyAsText()
            } catch (e: Exception) {
                log.e(e) { "Download failed" }
                mErrors.send(e.toMenzaError())
                return@withContext null
            }

            val data = try {
                log.i { "Scraping $menzaId" }
                scraper.scrape(request)
            } catch (e: WeekNotAvailable) {
                mErrors.send(MenzaError.WeekNotSupported)
                log.e { "Week not supported for $menzaId" }
                return@withContext null
            } catch (e: Exception) {
                mErrors.send(MenzaError.ParsingError(e))
                log.e(e) { "Parsing error" }
                return@withContext null
            }

            return@withContext data
        }.also { mRequestInProgress.value = false }
}
