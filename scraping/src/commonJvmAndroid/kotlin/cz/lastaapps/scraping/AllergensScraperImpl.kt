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

import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.day.FoodAllergens
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.Result
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.html5.img
import it.skrape.selects.html5.td

object AllergensScraperImpl : AllergenScraper<Result> {

    override suspend fun createRequestForAll() = skrape(AsyncFetcher) {
        request {
            this.url = "https://agata.suz.cvut.cz/jidelnicky/alergenyall.php"
        }
    }.scrape()

    override suspend fun createRequestForFood(foodId: FoodAllergens) = skrape(AsyncFetcher) {
        request {
            this.url = "https://agata.suz.cvut.cz/jidelnicky/alergeny.php?alergen=${foodId.id}"
        }
    }.scrape()

    override fun scrape(result: Result): Set<Allergen> {
        return result.htmlDocument { parseHtml() }
    }

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
