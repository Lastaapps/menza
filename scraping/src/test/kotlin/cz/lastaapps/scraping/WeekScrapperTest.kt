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
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeekScrapperTest {

    @Test
    fun scrapeWeek() {
        runBlocking {

            val date = LocalDate.now(CET)
            println("Loading for ${date.format(DateTimeFormatter.ISO_DATE)}")

            val weekFoodSet = WeekScrapper.scrapeWeek(MenzaId(1), WeekNumber.of(date))

            weekFoodSet.forEach {
                println(it)
            }

            assert(weekFoodSet.isNotEmpty())
            assert(weekFoodSet.map { it.foodType.type }.contains("Polévky"))
            assert(weekFoodSet.map { it.foodType.type }.contains("Specialita dne"))
        }
    }
}