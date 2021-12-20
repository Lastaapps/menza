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

import cz.lastaapps.entity.LocalTime
import cz.lastaapps.entity.TimeUtils
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.OpeningHours
import cz.lastaapps.entity.toCzechDayShortcutToDayOfWeek
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc

object OpeningHoursScrapper {

    suspend fun scrapOpeningHours(): Set<OpeningHours>? {

        var hours: Set<OpeningHours>? = null

        skrape(AsyncFetcher) {
            request {
                url = "https://agata.suz.cvut.cz/jidelnicky/oteviraci-doby.php"
            }
            response {
                htmlDocument {
                    hours = doParsing()
                }
            }
        }

        return hours
    }

    private fun Doc.doParsing(): Set<OpeningHours> {
        val set = mutableSetOf<OpeningHours>()

        findFirst("#otdoby") {
            children.forEachApply {
                val id = id.removePrefix("section").toInt()

                findAllAndCycle("table") {

                    val name = findFirst("thead tr th") {
                        ownText
                    }

                    findAllAndCycle("tbody tr") {
                        val startDay = children[0].ownText.toCzechDayShortcutToDayOfWeek()
                        val endDay = children[2].ownText.takeIf { it.removeSpaces().isNotBlank() }
                            ?.toCzechDayShortcutToDayOfWeek()
                        val startTime = children[3].ownText.parseTime()
                        val endTime = children[5].ownText.parseTime()
                        val type = children[6].ownText.removeSpaces().takeIf { it.isNotBlank() }

                        val days = TimeUtils.getDaysOfWeek()
                        val startIndex = days.indexOf(startDay)
                        val endIndex = endDay?.let { days.indexOf(it) } ?: startIndex

                        for (day in days.subList(startIndex, endIndex + 1)) {

                            set += OpeningHours(
                                MenzaId(id),
                                name,
                                day,
                                startTime, endTime,
                                type,
                            )
                        }
                    }
                }
            }
        }

        return set
    }

    private val timeRegex = "^([0-9]{1,2}):([0-9]{1,2})$".toRegex()

    private fun String.parseTime(): LocalTime? {

        val (sHours, sMinutes) = timeRegex.find(this)?.destructured ?: return null

        val hours = sHours.toIntOrNull() ?: return null
        val minutes = sMinutes.toIntOrNull() ?: return null

        if (hours == 0 && minutes == 0)
            return null

        return LocalTime(hours, minutes, 0)
    }
}