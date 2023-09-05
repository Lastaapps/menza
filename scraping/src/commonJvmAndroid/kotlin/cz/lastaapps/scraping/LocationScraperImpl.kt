/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

import cz.lastaapps.entity.menza.Address
import cz.lastaapps.entity.menza.Coordinates
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.MenzaLocation
import it.skrape.core.htmlDocument
import it.skrape.selects.Doc
import it.skrape.selects.html5.h3
import it.skrape.selects.html5.small

object LocationScraperImpl : LocationScraper {

    override suspend fun createRequest() = ContactsScraperImpl.createRequest()

    /**
     * Accepts contacts scrape result
     */
    override fun scrape(html: String): Set<MenzaLocation> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<MenzaLocation> {
        return emptySet()

        val set = mutableSetOf<MenzaLocation>()

        findFirst("#otdoby") {
            tryFindAllAndCycle("section") {
                val menzaId =
                    id.removePrefix("section").takeIf { it.removeSpaces().isNotBlank() }?.toInt()
                        ?: error("Invalid menza id")

                val address = h3 {
                    small {
                        findFirst {
                            text.removeSpaces()
                        }
                    }
                }
                val coordinates = findFirst(".span4 a") {
                    attributes["href"]!!.removeSpaces().parseLocation()
                }

                set += MenzaLocation(MenzaId(menzaId), Address(address), coordinates)
            }
        }

        return set
    }

    private val locationRegex = "q=(\\d+.\\d+),\\s*(\\d+.\\d+)&".toRegex()

    private fun String.parseLocation(): Coordinates {
        val (long, lat) = locationRegex.find(this)!!.destructured
        return Coordinates(long, lat)
    }
}
