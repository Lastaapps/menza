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
import cz.lastaapps.entity.exceptions.DishNameEmpty
import cz.lastaapps.entity.menza.MenzaId
import io.ktor.client.request.*
import it.skrape.core.htmlDocument
import it.skrape.selects.Doc
import it.skrape.selects.DocElement
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

object TodayScraperImpl : TodayScraper {

    override suspend fun createRequest(menzaId: MenzaId) =
        agataClient.get("index.php?clPodsystem=${menzaId.id}")

    override fun scrape(html: String): Set<Dish> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Dish> {

        val dishSet = mutableSetOf<Dish>()
        var currentType: String? = null
        var webOrder = 0
        var errorOccurred = false

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
                    try {
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
                            priceStudent?.let(::Price),
                            priceNormal?.let(::Price),
                            issuePlaces,
                        )
                    } catch (e: DishNameEmpty) {
                        e.printStackTrace()
                        errorOccurred = true
                    }
                }
                else -> error("No <tr> children")
            }
        }
        if (errorOccurred && dishSet.isEmpty())
            error("Failed to parse dish list - invalid server data")
        return dishSet
    }

    private fun DocElement.parseAllergens(): ImmutableList<AllergenId> {
        return tryFindFirst("img") {
            attributes["title"]!!
                .removePrefix("Alergeny: ")
                .split(',', ' ', '.')
                .filter { it.isNotBlank() }
                .map { AllergenId(it.toInt()) }
                .sortedBy { it.id }
                .toImmutableList()
        } ?: persistentListOf()
    }

    private fun DocElement.parseAllergensPage(): DishAllergensPage? {
        return tryFindFirst("a") {
            val code = attributes["href"]!!.removePrefix("alergeny.php?alergen=").toInt()
            DishAllergensPage(code)
        }
    }

    private fun DocElement.parseImage(): String? =
        tryFindFirst("img") {
            backendUrl + attribute("alt")
        }
}

private val moneyRegex = """(\d+([,|.]\d{1,2})?)?""".toRegex()

private fun DocElement.parseMoney(): Int? {
    val text = ownText.removeSpaces()
    if (text.isBlank()) return null
    val money = moneyRegex.find(text)?.destructured?.component1() ?: return null
    return money.replace(',', '.').toDouble().roundToInt()
}

private val issuePlaceIdRegex = """v(\d+)v(\d+)""".toRegex()
private fun DocElement.parseIssuePlaces(): ImmutableList<IssueLocation> =
    persistentListOf<IssueLocation>().mutate { issuePlaces ->
        findAllAndCycle("span") {
            val (windowId, terminalId) = issuePlaceIdRegex.find(id)!!.destructured
            issuePlaces += IssueLocation(
                terminalId.toInt(), windowId.toInt(), ownText, attribute("title"),
            )
        }
    }
