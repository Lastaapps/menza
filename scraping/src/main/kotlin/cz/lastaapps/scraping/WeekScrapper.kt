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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.FoodType
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekFood
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

object WeekScrapper {

    suspend fun scrapeWeek(menzaId: MenzaId, date: LocalDate): Set<WeekFood> {

        var set = emptySet<WeekFood>()

        skrape(AsyncFetcher) {
            request {
                url =
                    "https://agata.suz.cvut.cz/jidelnicky/indexTyden.php?clPodsystem=${menzaId.id}&clTyden=${
                        getWeekNumber(
                            date
                        )
                    }"
            }
            response {
                htmlDocument {
                    set = parse()
                }
            }
        }

        return set
    }

    private fun getWeekNumber(date: LocalDate): Int {
        return ChronoUnit.WEEKS.between(
            LocalDate.ofEpochDay(0),
            date
        ).toInt() + 25
    }

    private fun Doc.parse(): Set<WeekFood> {
        val set = mutableSetOf<WeekFood>()

        findFirst("#jidelnicek tbody") {

            var currentDate: LocalDate? = null

            children.forEachApply {
                //to skip day dividers
                if (children.first().className == "thkategorie") {

                    children[0].ownText.removeSpaces().takeIf { it.isNotBlank() }?.let {
                        currentDate = LocalDate.parse(
                            it, DateTimeFormatter.ofPattern(
                                "dd. MM. yyyy", Locale("cz")
                            )
                        )
                    }

                    val type = children[2].text.removeSpaces()
                    val amount: String? =
                        children[3].ownText.removeSpaces().takeIf { it.isNotBlank() }
                    val name = children[4].ownText.removeSpaces()

                    set += WeekFood(currentDate!!, FoodType(type), amount?.let { Amount(it) }, name)
                }
            }
        }

        return set
    }
}