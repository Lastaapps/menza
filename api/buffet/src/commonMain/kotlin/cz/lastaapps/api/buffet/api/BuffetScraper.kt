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

package cz.lastaapps.api.buffet.api

import arrow.core.Either
import arrow.core.Nel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.separateEither
import arrow.core.some
import arrow.core.toNonEmptyListOrNull
import cz.lastaapps.api.buffet.domain.model.dto.DishDayDto
import cz.lastaapps.api.buffet.domain.model.dto.DishDto
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.ParsingError
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import org.lighthousegames.logging.logging

internal interface BuffetScraper {
    fun matchValidity(html: String): Outcome<Pair<LocalDate, LocalDate>>
    fun matchContent(html: String): Outcome<ParsingRes<Pair<List<DishDayDto>, List<DishDayDto>>>>
}

private typealias ParsingRes<T> = Pair<Option<Nel<MenzaError>>, T>

internal class BuffetScraperImpl : BuffetScraper {

    companion object {

        private val log = logging()

        private val regexOptions =
            setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
        private const val toSkip = 18_000 // offset of useless characters at the beginning

        // Matchers date
        // d1 m1 y1 d2 m2 y2
        private val dateRegex = """Platný\D+(\d+)\D+(\d+)\D+(\d{4})\D+(\d+)\D+(\d+)\D+(\d{4})"""
            .toRegex(regexOptions)

        // Matches the table
        // content
        private val mainPartRegex = """<table[^>]*>(.*)</table>"""
            .toRegex(regexOptions)

        // Split buffets
        // fs, fel
        private val splitMainRegex = """(.*)</td>\s*<td(.*)"""
            .toRegex(regexOptions)

        // Matches days
        // name, content
        private val daysRegex = """<h\d>\s*([^3]*)\s*</h\d>\s*((?>(?!<h\d>).)+)"""
            .toRegex(regexOptions)

        // Matches dishes
        // type name price contains
        private val dishesRegex = """([^/]*):([^●]*)●\s*(\d+)[^(]*\(([^)]*)\)"""
            .toRegex(regexOptions)
    }

    override fun matchValidity(html: String): Outcome<Pair<LocalDate, LocalDate>> =
        html.matchDate()

    override fun matchContent(html: String): Outcome<ParsingRes<Pair<List<DishDayDto>, List<DishDayDto>>>> =
        html.matchMainPart()

    private fun String.matchDate(): Outcome<Pair<LocalDate, LocalDate>> =
        Either.catch {
            dateRegex.find(this, startIndex = toSkip)!!
                .groupValues
                .drop(1)
                .map { it.toInt() }
                .let {
                    Pair(
                        LocalDate(it[2], it[1], it[0]),
                        LocalDate(it[5], it[4], it[3]),
                    )
                }
        }.mapLeft {
            log.e(it) { "Parsing date range failed" }
            ParsingError.Buffet.DateRangeCannotBeParsed
        }

    private fun String.matchMainPart(): Outcome<ParsingRes<Pair<List<DishDayDto>, List<DishDayDto>>>> =
        Either.catch {
            mainPartRegex.find(this, startIndex = toSkip)!!
                .let { match ->
                    val (main) = match.destructured
                    main.splitMain()
                }
        }.mapLeft {
            log.e(it) { "Overall parsing failed" }
            ParsingError.Buffet.MenuCannotBeParsed
        }

    private fun String.splitMain(): ParsingRes<Pair<List<DishDayDto>, List<DishDayDto>>> =
        splitMainRegex.find(this)!!.let { match ->
            val (fs, fel) = match.destructured
            val fsData = fs.matchMainToDays()
            val felData = fel.matchMainToDays()
            val data = Pair(fsData.second, felData.second)

            val fsErrors = fsData.first.fold({ emptyList() }, { it })
            val felErrors = felData.first.fold({ emptyList() }, { it })
            when (val nel = (fsErrors + felErrors).toNonEmptyListOrNull()) {
                null -> ParsingRes(None, data)
                else -> ParsingRes(nel.some(), data)
            }
        }

    private fun String.matchMainToDays(): ParsingRes<List<DishDayDto>> =
        daysRegex.findAll(this).drop(1).map { match ->
            Either.catch {
                val (name, content) = match.destructured
                val dayOfWeek = name.removeHtml().toDayOfWeek()
                val res = content.findDays()

                res.first to DishDayDto(
                    dayOfWeek = dayOfWeek,
                    dishList = res.second,
                )
            }.mapLeft {
                log.e(it) { "Day parsing failed" }
                ParsingError.Buffet.DayCannotBeParsed
            }
        }
            .separateEither()
            // combine error together
            .let { (dayErrors, dishData) ->
                val valid = dishData.map { it.second }.toList()

                val errors = dishData.fold(
                    (dayErrors as Sequence<MenzaError>).toPersistentList()
                ) { acu, (errors, _) ->
                    when (errors) {
                        None -> acu
                        is Some -> acu.addAll(errors.value)
                    }
                }

                when (val nel = errors.toNonEmptyListOrNull()) {
                    null -> ParsingRes(None, valid)
                    else -> ParsingRes(nel.some(), valid)
                }
            }

    private fun String.findDays(): ParsingRes<List<DishDto>> =
        dishesRegex.findAll(this).mapIndexed { index, match ->
            Either.catch {
                val (type, name, price, contains) = match.destructured
                DishDto(
                    type = type.removeHtml(),
                    name = name.removeHtml(),
                    price = price.toInt(),
                    ingredients = contains.split(",").map { it.removeHtml() },
                    order = index,
                )
            }.mapLeft {
                log.e(it) { "Dish parsing failed" }
                ParsingError.Buffet.DishCannotBeParsed
            }
        }
            .separateEither()
            .let { (v1, v2) -> v1.toList() to v2.toList() }
            .let { (v1, v2) ->
                when (val nel = v1.toNonEmptyListOrNull()) {
                    null -> ParsingRes(None, v2)
                    else -> ParsingRes(nel.some(), v2)
                }
            }

    private val daysOfWeek = listOf(
        "pondělí", "úterý", "středa", "čtvrtek", "pátek", "sobota", "neděle",
    )

    private fun String.toDayOfWeek(): DayOfWeek {
        val index = daysOfWeek.indexOf(this.trim().lowercase())
        if (index < 0) error("Day of week name invalid: $this")
        return DayOfWeek.of(index + 1)
    }

    private val toRemove = arrayOf(
        """<[^>]*>""".toRegex() to "",
        """^.*>""".toRegex() to "",
        """<.*$""".toRegex() to "",
        """\s\s+""".toRegex() to " ",
    )

    private fun String.removeHtml() = this
        .let {
            toRemove.fold(it) { acu, (regex, replacement) ->
                acu.replace(regex, replacement)
            }
        }.trim()
}
