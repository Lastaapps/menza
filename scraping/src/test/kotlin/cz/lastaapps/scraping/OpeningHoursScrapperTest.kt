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

import cz.lastaapps.entity.LocalTime
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import org.junit.Test

class OpeningHoursScrapperTest {

    @Test
    @ExperimentalCoroutinesApi
    fun scrapOpeningHours() = runTest {

        val hours = OpeningHoursScrapper.scrapOpeningHours()


        hours.shouldNotBeNull()
        hours.shouldNotBeEmpty()

        val strahov = hours.filter { it.menzaId.id == 1 }
        strahov.shouldNotBeEmpty()

        val restaurant = strahov.filter { it.name == "Restaurace" }
        restaurant.shouldNotBeEmpty()

        val restaurantOpen = LocalTime(11, 0, 0)
        val restaurantClose = LocalTime(20, 30, 0)

        for (day in listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
        )) {
            val found = strahov.find { it.dayOfWeek == day }
            found.shouldNotBeNull()
            found.open shouldBe restaurantOpen
            found.close shouldBe restaurantClose
        }
        val found = strahov.find { it.dayOfWeek == DayOfWeek.FRIDAY }
        found.shouldNotBeNull()
        found.open shouldBe restaurantOpen
        found.close shouldBe LocalTime(19, 30, 0)
    }
}