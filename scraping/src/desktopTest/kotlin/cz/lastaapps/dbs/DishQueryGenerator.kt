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

package cz.lastaapps.dbs

import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.scraping.MenzaScraperImpl
import cz.lastaapps.scraping.OpeningHoursScraperImpl
import cz.lastaapps.scraping.TodayScraperImpl
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.random.nextInt

class DishQueryGenerator {
    @ExperimentalCoroutinesApi
    @Test
    fun scrapeMenzaInfo() = runTest() {
        val menzaList = MenzaScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }
        val sectors = OpeningHoursScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }.map { it.menzaId to it.locationName }.toSet()
        val menus = menzaList.map { menza ->
            menza.menzaId to TodayScraperImpl.run {
                scrape(createRequest(menza.menzaId).bodyAsText())
            }
        }

        insertDishes(sectors, menus)
    }

    private fun insertDishes(
        sectors: Set<Pair<MenzaId, String>>,
        menus: List<Pair<MenzaId, Set<Dish>>>
    ) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        menus.forEach { menu ->
            val localSectors = sectors.filter { it.first == menu.first }

            menu.second.forEach { dish ->
                val currentDishKey = SelectByDishName(dish.name)
                val currentSector = localSectors.random()

                QueryUtils.insert(
                    "jidlo",
                    listOf("jmeno"),
                    listOf(dish.name),
                )

                dish.allergens.forEach { allergenId ->
                    QueryUtils.insert(
                        "jidlo_alergen",
                        listOf("id_jidlo", "id_alergen"),
                        listOf(currentDishKey, allergenId.id),
                    )
                }
                QueryUtils.insert(
                    "nabidka",
                    listOf(
                        "datum", "id_jidlo", "id_sektor", "id_kuchar",
                        "cena_plna", "cena_sleva", "mnozstvi",
                    ),
                    listOf(
                        today, currentDishKey,
                        SectorQuery(currentSector), Random.nextInt(1..10),
                        dish.priceNormal.price, dish.priceStudent.price, dish.amount?.amount
                    ),
                )
                val buyer = if (Random.nextBoolean()) Random.nextInt(11..69) else null
                repeat(Random.nextInt(0..3)) {
                    QueryUtils.insert(
                        "prodej",
                        listOf("datum", "id_jidlo", "id_sektor", "id_osoba", "hodnoceni"),
                        listOf(
                            today,
                            currentDishKey, SectorQuery(currentSector),
                            buyer,
                            buyer?.let { if (Random.nextBoolean()) Random.nextInt(1..10) else null },
                        )
                    )
                }
                println()
            }
        }
    }
}

private class SelectByDishName(private val name: String) : Query {
    override fun resolve(): String =
        "SELECT id_jidlo FROM jidlo WHERE jmeno = '$name'"
}

private class SectorQuery(private val pair: Pair<MenzaId, String>) : Query {
    override fun resolve(): String =
        "SELECT id_sektor FROM sektor WHERE id_menza = ${pair.first.id} AND jmeno = '${pair.second}'"
}