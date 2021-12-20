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

    private val locationRegex = "q=(\\d+.\\d+),\\s*(\\d+.\\d+)&".toRegex()
    private const val openImgName = "img/Otevreno.png"
    private const val closeImgName = "img/Zavreno.png"

    suspend fun scrapeMenzaList(): Output {

        var opened = emptyMap<MenzaId, Opened>()
        var infomartions = emptyMap<MenzaId, TempMenza>()
        var contacts = emptySet<Contact>()
        var messages = emptyMap<MenzaId, String>()

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
            val info = infomartions[id]!!
            val message = messages[id]

            Menza(
                id,
                info.name,
                message,
                opened[id]!!,
                Address(info.address),
                info.mapLink
            )
        }.toSet()

        return Output(menzas, contacts)
    }

    suspend fun scrapeMenzaOpened(): Map<MenzaId, Opened> {
        var opened: Map<MenzaId, Opened>? = null

        skrape(AsyncFetcher) {
            request {
                url = "https://agata.suz.cvut.cz/jidelnicky/kontakty.php"
            }
            response {
                htmlDocument {
                    opened = parseMenzasOpened()
                }
            }
        }

        return opened ?: error("Failed to update opened")
    }

    private fun Doc.parseMenzasOpened(): Map<MenzaId, Opened> {

        val map = mutableMapOf<MenzaId, Opened>()

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

                        val open = if (opened) Opened.OPENED else Opened.CLOSED
                        map[MenzaId(id)] = open
                    }
                }
            }
        }
        return map
    }

    private fun Doc.parseNameAndAddress(): Map<MenzaId, TempMenza> {
        val map = mutableMapOf<MenzaId, TempMenza>()

        findFirst("#otdoby") {
            findAllAndCycle("section") {
                var name = ""
                var address = ""
                var mapLink: Location? = null

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
                            mapLink = attributes["href"]!!.removeSpaces().parseLocation()
                        }
                    }
                }

                map[MenzaId(menzaId)] = TempMenza(name, address, mapLink!!)
            }
        }

        return map
    }

    private fun String.parseLocation(): Location {
        val (long, lat) = locationRegex.find(this)!!.destructured
        return Location(long, lat)
    }

    private fun Doc.parseContacts(): Set<Contact> {
        val set = mutableSetOf<Contact>()

        findFirst("#otdoby") {
            findAll("section") {
                forEachApply {
                    val menzaId = id.removePrefix("section").toInt()

                    findAllAndCycle("tbody tr") {

                        var role: String? = null
                        var name: String? = null
                        var phoneNumber: String? = null
                        var email: String? = null

                        td {
                            findByIndex(0) {
                                role = text.takeIf { it.removeSpaces().isNotBlank() }
                            }
                            findByIndex(1) {
                                name = text.takeIf { it.removeSpaces().isNotBlank() }
                            }
                            findByIndex(2) {
                                a {
                                    findFirst {
                                        phoneNumber = attribute("href").takeIf {
                                            it.removeSpaces().isNotBlank()
                                        }?.removePrefix("tel:")
                                    }
                                }
                            }
                            findByIndex(3) {
                                a {
                                    findFirst {
                                        email =
                                            attribute("href").takeIf {
                                                it.removeSpaces().isNotBlank()
                                            }?.removePrefix("mailto:")?.decodeURLPart()
                                    }
                                }
                            }
                        }

                        if (role != null || name != null || phoneNumber != null || email != null)
                            set += Contact(MenzaId(menzaId), role, name, phoneNumber, email)
                    }
                }
            }
        }

        return set
    }

    private fun Doc.parseMessages(): Map<MenzaId, String> {
        val map = mutableMapOf<MenzaId, String>()

        findFirst("#aktuality") {
            findAllAndCycle("div div div") {
                val menzaId = id.removePrefix("Info").trim().toInt()
                var t = ""

                findByIndex(1, "p") {
                    t = text.replace("<BR>", "").replace("<br>", "")
                }
                map[MenzaId(menzaId)] = t
            }
        }

        return map
    }

    private data class TempMenza(
        val name: String, val address: String, val mapLink: Location,
    )

    data class Output internal constructor(
        val menzas: Set<Menza>,
        val contacts: Set<Contact>
    )
}