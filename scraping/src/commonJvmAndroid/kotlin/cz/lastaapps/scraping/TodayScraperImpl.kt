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
import cz.lastaapps.entity.day.Food
import cz.lastaapps.entity.day.FoodId
import cz.lastaapps.entity.day.IssueLocation
import cz.lastaapps.entity.menza.MenzaId
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.Result
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc

object TodayScraperImpl : TodayScraper<Result> {

    override suspend fun createRequest(menzaId: MenzaId) = skrape(AsyncFetcher) {
        request {
            url = "https://agata.suz.cvut.cz/jidelnicky/index.php?clPodsystem=${menzaId.id}"
        }
    }.scrape()

    override fun scrape(result: Result): Set<Food> {
        return result.htmlDocument { parseHtml() }
    }

    override fun scrape(html: String): Set<Food> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Food> {

        val food = mutableSetOf<Food>()
        var currentType: String? = null

        tryFindAllAndCycle("#jidelnicek tbody tr") {

            when (children.firstOrNull()?.tagName) {
                "th" -> {
                    currentType = children.first().ownText.removeSpaces()
                }
                "td" -> {

                    //TODO read real data!!!
                    val menzaId = -1

                    val amount =
                        children[1].ownText.removeSpaces().takeIf { it.isNotBlank() }
                    val name = children[2].ownText.removeSpaces()

                    val priceStudent = children[5]
                        .ownText.removeSpaces().removeSuffix(",00 Kč").toInt()
                    val priceNormal = children[6]
                        .ownText.removeSpaces().removeSuffix(",00 Kč").toInt()

                    val allergensFoodId: Int? = children[3].tryFindFirst("a") {
                        attribute("href").removePrefix("alergeny.php?alergen=")
                            .toInt()
                    }

                    val imgUrl: String? = children[4].tryFindFirst("a") {
                        attribute("href")/*.removeSpaces().let {
                            val toFind = "&xFile="
                            val index = it.indexOf(toFind)
                            it.substring(index + toFind.length)
                        }*/
                    }

                    val issuePlaces = mutableListOf<IssueLocation>()
                    children[7]
                    findAllAndCycle("span") {
                        issuePlaces += IssueLocation(
                            id,
                            ownText,
                            attribute("title"),
                        )
                    }

                    food += Food(
                        MenzaId(menzaId),
                        FoodType(currentType!!),
                        amount?.let { Amount(amount) },
                        name,
                        allergensFoodId?.let { FoodId(it) },
                        imgUrl,
                        Price(priceStudent),
                        Price(priceNormal),
                        issuePlaces,
                    )
                }
            }
        }
        return food
    }
}

