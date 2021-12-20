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

import cz.lastaapps.entity.menza.*
import io.ktor.http.*
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import it.skrape.selects.html5.*

object MenzaListScrapper {

    private const val openImgName = "img/Otevreno.png"
    private const val closeImgName = "img/Zavreno.png"

    suspend fun scrapeMenzaList(): Output {

        var opened = emptyMap<Int, Boolean>()
        var infomartions = emptyMap<Int, TempMenza>()
        var contacts = emptySet<Contact>()
        var messages = emptyMap<Int, String>()

        skrape(AsyncFetcher) {
            request {
                url = "https://agata.suz.cvut.cz/jidelnicky/kontakty.php"
            }
            response {
                htmlDocument {
                    opened = parseMenzasOpened()
                    infomartions = parseNameAndAddress()
                    contacts = parseContacts()
                }
            }
        }
        skrape(AsyncFetcher) {
            request {
                url = "https://agata.suz.cvut.cz/jidelnicky/index.php"
            }
            response {
                htmlDocument {
                    messages = parseMessages()
                }
            }
        }

        val commonIds = opened.keys.intersect(infomartions.keys)

        val menzas = commonIds.map { id ->
            val open = if (opened[id]!!) Opened.OPENED else Opened.CLOSED
            val info = infomartions[id]!!
            val message = messages[id]

            Menza(
                MenzaId(id),
                info.name,
                message,
                open,
                Address(info.address),
                info.mapLink
            )
        }.toSet()

        return Output(menzas, contacts)
    }

    private fun Doc.parseMenzasOpened(): Map<Int, Boolean> {

        val map = mutableMapOf<Int, Boolean>()

        findFirst("#menzy") {
            li {
                a {
                    findAllAndCycle {
                        var opened = false

                        //val name = ownText.trim()
                        val id = id.removePrefix("podSh").toInt()

                        img {
                            findFirst {
                                opened = attributes["src"] == openImgName
                            }
                        }

                        map[id] = opened
                    }
                }
            }
        }
        return map
    }

    private fun Doc.parseNameAndAddress(): Map<Int, TempMenza> {
        val map = mutableMapOf<Int, TempMenza>()

        findFirst("#otdoby") {
            findAllAndCycle("section") {
                var name = ""
                var address = ""
                var mapLink = ""

                val menzaId = id.removePrefix("section").toInt()

                h3 {
                    findFirst {
                        name = this.ownText.removeSpaces()
                    }
                    small {
                        findFirst {
                            address = text.removeSpaces()
                        }
                    }
                }
                findFirst(".span4") {
                    a {
                        findFirst {
                            //TODO extract coordinates
                            mapLink = attributes["href"]!!.trim()
                        }
                    }
                }

                map[menzaId] = TempMenza(name, address, mapLink)
            }
        }

        return map
    }

    private fun Doc.parseContacts(): Set<Contact> {
        val set = mutableSetOf<Contact>()

        findFirst("#otdoby") {
            findAll("section") {
                forEachApply {
                    val menzaId = id.removePrefix("section").toInt()

                    var role = ""
                    var name = ""
                    var phoneNumber = ""
                    var email = ""

                    findAllAndCycle("tbody tr") {
                        td {
                            findByIndex(0) {
                                role = text.trim()
                            }
                            findByIndex(1) {
                                name = text.trim()
                            }
                            findByIndex(2) {
                                a {
                                    findFirst {
                                        phoneNumber =
                                            attribute("href").removePrefix("tel:")
                                    }
                                }
                            }
                            findByIndex(3) {
                                a {
                                    findFirst {
                                        email =
                                            attribute("href").removePrefix("mailto:")
                                                .decodeURLPart()
                                    }
                                }
                            }
                        }

                        set += Contact(MenzaId(menzaId), role, name, phoneNumber, email)
                    }
                }
            }
        }

        return set
    }

    private fun Doc.parseMessages(): Map<Int, String> {
        val map = mutableMapOf<Int, String>()

        findFirst("#aktuality") {
            findAllAndCycle("div div div") {
                val menzaId = id.removePrefix("Info").trim().toInt()
                var t = ""

                findByIndex(1, "p") {
                    t = text.replace("<BR>", "").replace("<br>", "")
                }
                map[menzaId] = t
            }
        }

        return map
    }

    private data class TempMenza(
        val name: String, val address: String, val mapLink: String,
    )

    data class Output internal constructor(
        val menzas: Set<Menza>,
        val contacts: Set<Contact>
    )
}