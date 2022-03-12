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

import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.Message
import io.ktor.client.request.*
import it.skrape.core.htmlDocument
import it.skrape.selects.Doc

object MessagesScraperImpl : MessagesScraper {

    override suspend fun createRequest() =
        agataClient.get("https://agata.suz.cvut.cz/jidelnicky/index.php")

    /**
     * Accepts any url /jidelnicky/index.php???
     */
    override fun scrape(html: String): Set<Message> {
        return htmlDocument(html) { parseHtml() }
    }

    private fun Doc.parseHtml(): Set<Message> {
        val set = mutableSetOf<Message>()

        findFirst("#aktuality") {
            tryFindAllAndCycle("div div div") {
                val menzaId =
                    id.removePrefix("Info").removeSpaces().takeIf { it.isNotBlank() }?.toInt()
                        ?: error("Invalid menza id")

                val text = findByIndex(1, "p") {
                    html
                        .replace("<BR>", "\n")
                        .replace("<br>", "\n")
                        .replace(" +".toRegex(), " ")
                        .replace("^ +".toRegex(RegexOption.MULTILINE), "")
                        .replace(" +$".toRegex(RegexOption.MULTILINE), "")
                        .removeSpaces()
                }
                set += Message(MenzaId(menzaId), text)
            }
        }

        return set
    }
}
