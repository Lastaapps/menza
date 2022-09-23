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

import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.day.DishAllergensPage
import io.ktor.client.request.*
import it.skrape.core.htmlDocument
import it.skrape.selects.Doc
import it.skrape.selects.html5.img
import it.skrape.selects.html5.td

object AllergenScraperImpl : AllergenScraper {

    override suspend fun createRequestForAll() =
        agataClient.get("alergenyall.php")

    override suspend fun createRequestForDish(dishId: DishAllergensPage) =
        agataClient.get("alergeny.php?alergen=${dishId.pageId}")

    override fun scrape(html: String): Set<Allergen> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Allergen> {
        val set = mutableSetOf<Allergen>()

        findFirst("#otdoby tbody") {
            tryFindAllAndCycle("tr") {

                if (attributes["style"]?.contains("none") == true) {
                    return@tryFindAllAndCycle
                }

                td {
                    val allergenId = findByIndex(0) {
                        img {
                            findFirst {
                                attributes["alt"]?.removeSpaces()
                                    ?.takeIf { it.isNotBlank() }?.toInt()
                                    ?: error("Blank allergen code")
                            }
                        }
                    }
                    val name = findByIndex(1) { ownText }
                    val description = findByIndex(2) { ownText }

                    set += Allergen(AllergenId(allergenId), name, description)
                }
            }
        }

        return set
    }
}
