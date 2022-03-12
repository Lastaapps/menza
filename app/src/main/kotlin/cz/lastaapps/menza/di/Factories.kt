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

package cz.lastaapps.menza.di

import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.scraping.TodayScraper
import cz.lastaapps.scraping.WeekScraper
import cz.lastaapps.storage.repo.TodayRepo
import cz.lastaapps.storage.repo.TodayRepoImpl
import cz.lastaapps.storage.repo.WeekRepo
import cz.lastaapps.storage.repo.WeekRepoImpl
import javax.inject.Inject

interface TodayRepoFactory {
    fun create(menzaId: MenzaId): TodayRepo
}

class TodayRepoFactoryImpl @Inject constructor(
    private val todayScraper: TodayScraper,
) : TodayRepoFactory {
    override fun create(menzaId: MenzaId): TodayRepo {
        return TodayRepoImpl(scraper = todayScraper, menzaId = menzaId)
    }
}


interface WeekRepoFactory {
    fun create(menzaId: MenzaId): WeekRepo
}

class WeekRepoFactoryImpl @Inject constructor(
    private val weekScraper: WeekScraper,
) : WeekRepoFactory {
    override fun create(menzaId: MenzaId): WeekRepo {
        return WeekRepoImpl(scraper = weekScraper, menzaId = menzaId)
    }
}