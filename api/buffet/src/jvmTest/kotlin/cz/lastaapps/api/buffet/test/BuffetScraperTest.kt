/*
 *    Copyright 2026, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.api.buffet.test

import arrow.core.Either.Right
import arrow.core.Ior
import arrow.core.Nel
import arrow.core.None
import arrow.core.Option
import arrow.core.flatten
import arrow.core.left
import co.touchlab.kermit.Logger
import cz.lastaapps.api.buffet.api.BuffetApi
import cz.lastaapps.api.buffet.api.BuffetApiImpl
import cz.lastaapps.api.buffet.api.BuffetScraperImpl
import cz.lastaapps.api.buffet.data.model.DishDayDto
import cz.lastaapps.api.buffet.data.model.WebContentDto
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.SyncError
import cz.lastaapps.core.domain.error.DomainError
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import kotlinx.datetime.LocalDate

class BuffetScraperTest :
    StringSpec(
        {
            fun loadPage(name: String): String =
                BuffetScraperTest::class.java.classLoader
                    ?.getResource(name)!!
                    .readText()

            fun scraper() = BuffetScraperImpl()

            fun client() = HttpClient()

            "Scrape live" {
                val log = Logger.withTag("Live")
                val api: BuffetApi = BuffetApiImpl(client(), scraper())
                val res = api.process()
                res.map {
                    log.i { "${it.from} ${it.to}" }
                    log.i { it.fs.toString() }
                    log.i { it.fel.toString() }
                }
                res.shouldBeInstanceOf<Ior.Right<WebContentDto>>()
                val value = res.value
                listOf(value.fs, value.fel).forEach { type ->
                    type.shouldHaveSize(5)
                }
            }

            "Scrape 2025-12-15" {
                val log = Logger.withTag("2025-12-15")
                val html = loadPage("2025-12-15.html")
                val scraper = scraper()
                val date = scraper.matchValidity(html)
                val content = scraper.matchContent(html)

                log.i { date.toString() }
                log.i { content.toString() }

                dateRangeTest(
                    date,
                    LocalDate(2025, 1, 13),
                    LocalDate(2025, 1, 17),
                )

                val (fs, fel) = testDeconstruct(content)

                commonTest(fs, listOf(3, 3, 3, 3, 3))
                commonTest(fel, listOf(3, 3, 4, 4, 3))
                @Suppress("SpellCheckingInspection")
                fs[1].dishList[2].run {
                    type shouldBe "Hlavní jídlo 2"
                    name shouldBe "Kuřecí steak s mexickou salsou, hranolky"
                    price shouldBe 149
                }
                fs[1].dishList[0].run {
                    type shouldBe "Polévka"
                    name shouldBe "Gulášová"
                    price shouldBe 40
                }
                // A "mistake" is present on the website where a label exists twice
                fs[4].dishList[1].type shouldBe fs[4].dishList[2].type

                @Suppress("SpellCheckingInspection")
                fel[0].dishList[1].run {
                    type shouldBe "Hlavní jídlo 1"
                    name shouldBe "Segedínský guláš, houskové knedlíky"
                    price shouldBe 144
                }
                fel[4].dishList[2].run {
                    type shouldBe "Hlavní jídlo 2"
                    name shouldBe "Smažený kuřecí řízek, domácí bramborový salát"
                    price shouldBe 149
                }
            }

            "Scrape summer" {
                val log = Logger.withTag("summer")
                val html = loadPage("summer.html")
                val scraper = scraper()
                val date = scraper.matchValidity(html)

                log.i { "Got $date" }
                date shouldBe SyncError.Closed.left()
            }
        },
    )

private fun dateRangeTest(
    date: Outcome<Pair<LocalDate, LocalDate>>,
    from: LocalDate,
    to: LocalDate,
) {
    date.shouldBeInstanceOf<Right<Pair<LocalDate, LocalDate>>>()
    date.value.first shouldBe from
    date.value.second shouldBe to
}

private fun testDeconstruct(
    content: Outcome<Pair<Option<Nel<DomainError>>, Pair<List<DishDayDto>, List<DishDayDto>>>>,
): Pair<List<DishDayDto>, List<DishDayDto>> {
    content.shouldBeInstanceOf<Right<Pair<Option<Nel<DomainError>>, Pair<List<DishDayDto>, List<DishDayDto>>>>>()
    content.value.first shouldBe None
    val (fs, fel) = content.value.second
    return fs to fel
}

private fun commonTest(
    buffet: List<DishDayDto>,
    days: List<Int>,
) {
    buffet.map { it.dishList.size } shouldBe days
    // checks if all the html parts were successfully removed
    buffet
        .map { day ->
            day.dishList.map { listOf(it.name, it.type) }
        }.flatten()
        .flatten()
        .forEach {
            it shouldNotContain "<"
            it shouldNotContain ">"
        }
}
