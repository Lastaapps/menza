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

package cz.lastaapps.scraping

import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.common.Price
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.day.DishAllergensPage
import cz.lastaapps.entity.day.IssueLocation
import cz.lastaapps.entity.menza.MenzaId
import it.skrape.core.htmlDocument
import it.skrape.fetcher.Result
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.DocElement

object TodayScraperImpl : TodayScraper<Result> {

    override suspend fun createRequest(menzaId: MenzaId) = skrape(CIOAsyncFetcher) {
        request {
            url = "https://agata.suz.cvut.cz/jidelnicky/index.php?clPodsystem=${menzaId.id}"
        }
    }.scrape()

    override fun scrape(result: Result): Set<Dish> {
        return result.htmlDocument { parseHtml() }
    }

    override fun scrape(html: String): Set<Dish> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Dish> {

        val dishSet = mutableSetOf<Dish>()
        var currentType: String? = null
        var webOrder = 0

        val menzaId = findFirst("body #PodsysActive") {
            attribute("value").removeSpaces().takeIf { it.isNotBlank() }?.toInt()
        } ?: error("Menza id not found")

        tryFindAllAndCycle("#jidelnicek table tbody tr") {

            when (children.firstOrNull()?.tagName) {
                "th" -> {
                    val newType = children.first().ownText.removeSpaces()
                    if (newType != currentType) {
                        currentType = newType
                        webOrder++
                    }
                }
                "td" -> {
                    val amount = children[1].ownText.removeSpaces().takeIf { it.isNotBlank() }
                    val name = children[2].ownText.removeSpaces()
                    val dishAllergens = children[3].parseAllergens()
                    val dishAllergensPage = children[3].parseAllergensPage()
                    val imgUrl = children[4].parseImage()
                    val priceStudent = children[5].parseMoney()
                    val priceNormal = children[6].parseMoney()
                    val issuePlaces = children[7].parseIssuePlaces()

                    dishSet += Dish(
                        MenzaId(menzaId),
                        CourseType(currentType!!, webOrder),
                        amount?.let { Amount(amount) },
                        name,
                        dishAllergens,
                        dishAllergensPage,
                        imgUrl,
                        Price(priceStudent),
                        Price(priceNormal),
                        issuePlaces,
                    )
                }
                else -> error("No <tr> children")
            }
        }
        return dishSet
    }

    private fun DocElement.parseAllergens(): List<AllergenId> {
        return tryFindFirst("a") {
            attributes["title"]!!
                .removePrefix("Alergeny: ")
                .split(',', ' ')
                .filter { it.isNotBlank() }
                .map { AllergenId(it.toInt()) }
                .sortedBy { it.id }
        } ?: emptyList()
    }

    private fun DocElement.parseAllergensPage(): DishAllergensPage? {
        return tryFindFirst("a") {
            val code = attributes["href"]!!.removePrefix("alergeny.php?alergen=").toInt()
            DishAllergensPage(code)
        }
    }

    private fun DocElement.parseImage(): String? {
        return tryFindFirst("a") {
            "https://agata.suz.cvut.cz/jidelnicky/" +
                    attribute("href").replace("imgshow.php", "showfoto.php")
            /*.removeSpaces().let {
                val toFind = "&xFile="
                val index = it.indexOf(toFind)
                it.substring(index + toFind.length)
            }*/
        }
    }

    private val moneyRegex = "(\\d+)([,|.]\\d{2})?".toRegex()

    private fun DocElement.parseMoney(): Int {
        val text = ownText.removeSpaces()
        val money = moneyRegex.find(text)!!.destructured.component1()
        return money.toInt()
    }

    private val issuePlaceIdRegex = "v(\\d+)v(\\d+)".toRegex()
    private fun DocElement.parseIssuePlaces(): List<IssueLocation> {
        val issuePlaces = mutableListOf<IssueLocation>()
        findAllAndCycle("span") {
            val (windowId, terminalId) = issuePlaceIdRegex.find(id)!!.destructured
            issuePlaces += IssueLocation(
                terminalId.toInt(), windowId.toInt(), ownText, attribute("title"),
            )
        }
        return issuePlaces
    }
}

