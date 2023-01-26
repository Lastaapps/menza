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
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Ior
import arrow.core.Nel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.leftIor
import arrow.core.nel
import arrow.core.rightIor
import arrow.core.separateEither
import arrow.core.some
import arrow.core.toNonEmptyListOrNull
import cz.lastaapps.api.buffet.domain.model.dto.DishDayDto
import cz.lastaapps.api.buffet.domain.model.dto.DishDto
import cz.lastaapps.api.buffet.domain.model.dto.WebContentDto
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.OutcomeIor
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.ParsingError
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.catchingNetwork
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

internal interface BuffetApi {
    suspend fun process(): OutcomeIor<WebContentDto>
}

private typealias ParsingRes<T> = Pair<Option<Nel<MenzaError>>, T>

internal class BuffetApiImpl(
    private val client: HttpClient,
) : BuffetApi {
    override suspend fun process(): OutcomeIor<WebContentDto> = outcome {
        val text = catchingNetwork { getPageText() }.bind()
        val (from, to) = text.matchDate().bind()
        val (errors, buffets) = text.matchMainPart().bind()
        val (fs, fel) = buffets

        val res = WebContentDto(
            from = from,
            to = to,
            fs = fs,
            fel = fel,
        )

        when (errors) {
            None -> res.rightIor()
            is Some -> Ior.Both(errors.value, res)
        }
    }.let {
        when (it) {
            is Left -> return it.value.nel().leftIor()
            is Right -> it.value
        }
    }

    private suspend fun getPageText() =
        client
            .get("http://studentcatering.cz/jidelni-listek/")
            .bodyAsText()

    companion object {
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
        private val splitMainRegex = """(.*)</td>.<td(.*)"""
            .toRegex(regexOptions)

        // Matches days
        // name, content
        private val daysRegex = """<h3><strong>([^3]*)</strong></h3>.((?>(?!<h3>).)+)"""
            .toRegex(regexOptions)

        // Matches dishes
        // type name price contains
        private val dishesRegex =
            """<p><strong>([^>]*):</strong><br />.([^●]*)●\s*(\d+)[^(]*\(([^)]*)\)"""
                .toRegex(regexOptions)
    }

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
            ParsingError.Buffet.DishListCannotBeParsed
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
        daysRegex.findAll(this).map { match ->
            Either.catch {
                val (name, content) = match.destructured
                val dayOfWeek = name.toDayOfWeek()
                val res = content.findDays()

                res.first to DishDayDto(
                    dayOfWeek = dayOfWeek,
                    dishList = res.second,
                )
            }.mapLeft {
                ParsingError.Buffet.DishCannotBeParsed
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
        dishesRegex.findAll(this).map { match ->
            Either.catch {
                val (type, name, price, contains) = match.destructured
                DishDto(
                    type = type,
                    name = name,
                    price = price.toInt(),
                    ingredients = contains.split(",").map { it.trim() }
                )
            }.mapLeft {
                ParsingError.Buffet.DayCannotBeParsed
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
        "PONDĚLÍ", "ÚTERÝ", "STŘEDA", "ČTVRTEK", "PÁTEK", "SOBOTA", "NEDĚLE",
    )

    private fun String.toDayOfWeek() =
        DayOfWeek.of(daysOfWeek.indexOf(this) + 1)

}
