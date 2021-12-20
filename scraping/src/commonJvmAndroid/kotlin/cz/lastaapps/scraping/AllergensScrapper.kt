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
import cz.lastaapps.entity.day.FoodId
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.html5.img
import it.skrape.selects.html5.td

object AllergensScrapper {

    suspend fun scrapAllAllergens(): Set<Allergen> {
        return scrapeAllergens("https://agata.suz.cvut.cz/jidelnicky/alergenyall.php")
    }

    suspend fun scrapFoodAllergens(foodId: FoodId): Set<Allergen> {
        return scrapeAllergens("https://agata.suz.cvut.cz/jidelnicky/alergeny.php?alergen=${foodId.id}")
    }

    private suspend fun scrapeAllergens(url: String): Set<Allergen> {

        var toReturn: Set<Allergen>? = null

        skrape(AsyncFetcher) {
            request {
                this.url = url
            }
            response {
                htmlDocument {
                    toReturn = scrapeHtml()
                }
            }
        }

        return toReturn ?: error("Scrapping failed")
    }

    private fun Doc.scrapeHtml(): Set<Allergen> {
        val set = mutableSetOf<Allergen>()
        findAllAndCycle("#otdoby tbody tr") {
            var allergenId = 0
            var name = ""
            var description = ""

            td {
                findByIndex(0) {
                    img {
                        findFirst {
                            allergenId = attribute("alt").trim().toInt()
                        }
                    }
                }
                findByIndex(1) {
                    name = ownText
                }
                findByIndex(2) {
                    description = ownText
                }
            }

            set += Allergen(AllergenId(allergenId), name, description)
        }

        return set
    }
}