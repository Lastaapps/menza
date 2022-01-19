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

import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.entity.week.WeekNotAvailable
import cz.lastaapps.entity.week.WeekNumber
import cz.lastaapps.scraping.WeekScraper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class WeekRepoImpl <R: Any> (
    private val scraper: WeekScraper<R>,
    private val menzaId: MenzaId,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): WeekRepo {

    companion object {
        private val log = logging(WeekRepo::class.simpleName)
    }

    override val errors: Channel<Errors>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<Errors>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    private val mutex = Mutex()

    override suspend fun getData(): Set<WeekDish>? = mutex.withLock {
        withContext(dispatcher) {
            val request = try {
                log.i { "Getting data from a server" }
                scraper.createRequest(menzaId, WeekNumber.tempWeekNumber)
            } catch (e: Exception) {
                mErrors.send(Errors.ConnectionError)
                return@withContext null
            }

            val data = try {
                log.i { "Scraping" }
                scraper.scrape(request)
            } catch (e: WeekNotAvailable) {
                //TODO week no available
                return@withContext null
            } catch (e: Exception) {
                mErrors.send(Errors.ParsingError)
                return@withContext null
            }
            return@withContext data
        }
    }
}
