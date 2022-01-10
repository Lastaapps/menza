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
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.entity.week.WeekNumber
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.OpeningHoursScraper
import cz.lastaapps.scraping.WeekScraper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

class WeekRepoImpl <R: Any> (
    private val scraper: WeekScraper<R>,
    private val menzaId: MenzaId,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
): WeekRepo {

    override val errors: Channel<Errors>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<Errors>(Channel.CONFLATED)
    private val mRequestInProgress = MutableStateFlow(false)

    private val mutex = Mutex()

    override suspend fun getData(): Set<WeekDish>? = mutex.withLock {
        withContext(dispatcher) {
            val request = try {
                scraper.createRequest(menzaId, WeekNumber.tempWeekNumber)
            } catch (e: Exception) {
                mErrors.send(Errors.ConnectionError)
                return@withContext null
            }

            val data = try {
                scraper.scrape(request)
            } catch (e: Exception) {
                mErrors.send(Errors.ParsingError)
                return@withContext null
            }
            return@withContext data
        }
    }
}
