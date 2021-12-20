/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.scraping

import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekNumber
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.todayAt
import org.junit.Test

class WeekScrapperTest {

    @ExperimentalCoroutinesApi
    @Test
    fun scrapeWeek() = runTest {

        val date = Clock.System.todayAt(CET)
        println("Loading for $date")

        val weekFoodSet = WeekScrapper.scrapeWeek(MenzaId(1), WeekNumber.of(date))

        weekFoodSet.forEach {
            println(it)
        }

        weekFoodSet.shouldNotBeNull()
        weekFoodSet.shouldNotBeEmpty()
        weekFoodSet.map { it.foodType.type } shouldContain "Polévky"
        weekFoodSet.map { it.foodType.type } shouldContain "Specialita dne"
    }
}