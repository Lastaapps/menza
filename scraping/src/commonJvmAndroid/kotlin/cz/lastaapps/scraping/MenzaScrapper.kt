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

import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.Opened
import it.skrape.core.htmlDocument
import it.skrape.fetcher.Result
import it.skrape.selects.Doc
import it.skrape.selects.html5.img

object MenzaScrapper : Scrapper<Menza> {

    private const val openImgName = "img/Otevreno.png"
    private const val closeImgName = "img/Zavreno.png"

    /**
     * Accepts all the results
     */
    override fun scrape(result: Result): Set<Menza> {
        return result.htmlDocument { parseHtml() }
    }

    override fun scrape(html: String): Set<Menza> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Menza> {

        val set = mutableSetOf<Menza>()

        findFirst("#menzy") {
            tryFindAllAndCycle("li a") {

                val name = ownText.removeSpaces()
                val id = id.removePrefix("podSh").toInt()

                val opened = img {
                    findFirst {
                        when (val imgName = attributes["src"]) {
                            openImgName -> true
                            closeImgName -> false
                            else -> error("Illegal open state image: $imgName")
                        }
                    }
                }

                val open = if (opened) Opened.OPENED else Opened.CLOSED
                set += Menza(MenzaId(id), name, open)
            }
        }
        return set
    }
}