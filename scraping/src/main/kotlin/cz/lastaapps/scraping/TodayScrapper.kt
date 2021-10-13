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
import cz.lastaapps.entity.common.Price
import cz.lastaapps.entity.day.Day
import cz.lastaapps.entity.day.Food
import cz.lastaapps.entity.day.FoodId
import cz.lastaapps.entity.day.IssueLocation
import cz.lastaapps.entity.menza.MenzaId
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import java.time.ZonedDateTime

object TodayScrapper {

    suspend fun scrapeToday(menzaId: MenzaId): Day {
        val localDate = ZonedDateTime.now(CET).toLocalDate()
        val food = mutableListOf<Food>()

        skrape(AsyncFetcher) {
            request {
                url = "https://agata.suz.cvut.cz/jidelnicky/index.php?clPodsystem=${menzaId.id}"
            }
            response {
                htmlDocument {

                    var currentType: String? = null

                    tryFindAllAndCycle("#jidelnicek tbody tr") {

                        when (children.firstOrNull()?.tagName) {
                            "th" -> {
                                currentType = children.first().ownText.removeSpaces()
                            }
                            "td" -> {

                                val amount =
                                    children[1].ownText.removeSpaces().takeIf { it.isNotBlank() }
                                val name = children[2].ownText.removeSpaces()

                                val priceStudent = children[5]
                                    .ownText.removeSpaces().removeSuffix(",00 Kč").toInt()
                                val priceNormal = children[6]
                                    .ownText.removeSpaces().removeSuffix(",00 Kč").toInt()

                                var allergensFoodId: Int? = null
                                children[3].tryFindFirst("a") {
                                    allergensFoodId =
                                        attribute("href").removePrefix("alergeny.php?alergen=")
                                            .toInt()
                                }

                                var imgName: String? = null
                                children[4].tryFindFirst("a") {
                                    imgName = attribute("href").removeSpaces().let {
                                        val toFind = "&xFile="
                                        val index = it.indexOf(toFind)
                                        it.substring(index + toFind.length)
                                    }
                                }

                                val issuePlaces = mutableListOf<IssueLocation>()
                                children[7]
                                findAllAndCycle("span") {
                                    issuePlaces += IssueLocation(
                                        menzaId,
                                        id,
                                        ownText,
                                        attribute("title"),
                                    )
                                }

                                food += Food(
                                    FoodType(currentType!!),
                                    amount?.let { Amount(amount) },
                                    name,
                                    allergensFoodId?.let { FoodId(it) },
                                    imgName,
                                    Price(priceStudent),
                                    Price(priceNormal),
                                    issuePlaces,
                                )
                            }
                        }
                    }
                }
            }
        }

        return Day(localDate, food)
    }
}