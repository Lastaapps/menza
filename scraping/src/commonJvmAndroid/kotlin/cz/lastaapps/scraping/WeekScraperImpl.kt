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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.entity.week.WeekNotAvailable
import cz.lastaapps.entity.week.WeekNumber
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.Result
import it.skrape.fetcher.skrape
import it.skrape.selects.Doc
import kotlinx.datetime.LocalDate

object WeekScraperImpl : WeekScraper<Result> {

    private val dateRegex = "^([0-9]{1,2}). ([0-9]{1,2}). ([0-9]{4})$".toRegex()

    override suspend fun createRequest(
        menzaId: MenzaId,
        @Suppress("UNUSED_PARAMETER") weekNumber: WeekNumber
    ) = skrape(AsyncFetcher) {
        request {
            url =
                "https://agata.suz.cvut.cz/jidelnicky/indexTyden.php?clPodsystem=${menzaId.id}"
            //TODO week numbers are not working
            //+ "&clTyden=${weekNumber.week}"
        }
    }.scrape()

    @Throws(WeekNotAvailable::class)
    override fun scrape(result: Result): Set<WeekDish> {
        return result.htmlDocument { parseHtml() }
    }

    @Throws(WeekNotAvailable::class)
    override fun scrape(html: String): Set<WeekDish> {
        return htmlDocument(html) { parseHtml() }
    }

    @Throws(WeekNotAvailable::class)
    private fun Doc.parseHtml(): Set<WeekDish> {
        val set = mutableSetOf<WeekDish>()

        tryFindFirst(".data") {
            if (ownText == "Tato provozovna nevystavuje týdenní jídelní lístek.") {
                throw WeekNotAvailable()
            }
        }

        findFirst("#jidelnicek tbody") {

            var currentDate: LocalDate? = null

            children.forEachApply {
                //to skip day dividers
                if (children.first().className == "thkategorie") {

                    children[0].ownText.removeSpaces().takeIf { it.isNotBlank() }?.let {
                        val values = dateRegex.find(it)?.destructured!!
                        val (day, month, year) = values
                        currentDate = LocalDate(
                            year.toInt(), month.toInt(), day.toInt()
                        )
                    }

                    val type = children[2].text.removeSpaces()
                    val amount: String? =
                        children[3].ownText.removeSpaces().takeIf { it.isNotBlank() }
                    val name = children[4].ownText.removeSpaces()

                    if (name.isNotBlank() && name.isNameValid()) {
                        set += WeekDish(
                            currentDate!!,
                            CourseType(type),
                            amount?.let { Amount(it) },
                            name,
                        )
                    }
                }
            }
        }

        return set
    }

    //TODO add more celebrations
    private val invalidDishNames = listOf("štědrýden", "zavřeno")

    /**
     * Checks if the name is valid food name e.g. it is Christmas, Closed, ...
     */
    private fun String.isNameValid(): Boolean {
        if (this.lowercase().replace("\\s+".toRegex(), "") in invalidDishNames) return false
        return true
    }
}