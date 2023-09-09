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

package cz.lastaapps.api.buffet.test

import arrow.core.Either.Right
import arrow.core.Ior
import arrow.core.Nel
import arrow.core.None
import arrow.core.Option
import arrow.core.flatten
import arrow.core.left
import cz.lastaapps.api.buffet.api.BuffetApi
import cz.lastaapps.api.buffet.api.BuffetApiImpl
import cz.lastaapps.api.buffet.api.BuffetScraperImpl
import cz.lastaapps.api.buffet.data.model.DishDayDto
import cz.lastaapps.api.buffet.data.model.WebContentDto
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.SyncError
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.util.doAFuckingSetupForTestBecauseThisShitIsNiceButBroken
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import kotlinx.datetime.LocalDate
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.logging

class BuffetScraperTest : StringSpec(
    {

        KmLogging.doAFuckingSetupForTestBecauseThisShitIsNiceButBroken()

        fun loadPage(name: String): String =
            BuffetScraperTest::class.java.classLoader?.getResource(name)!!.readText()

        fun scraper() = BuffetScraperImpl()
        fun client() = HttpClient()

        "Scrape live" {
            val log = logging("Live")
            val api: BuffetApi = BuffetApiImpl(client(), scraper())
            val res = api.process()
            res.map {
                log.i { "${it.from} ${it.to}" }
                log.i { it.fs }
                log.i { it.fel }
            }
            res.shouldBeInstanceOf<Ior.Right<WebContentDto>>()
            val value = res.value
            listOf(value.fs, value.fel).forEach { type ->
                type.shouldHaveSize(5)
            }
        }

        "Scrape 2023-01-23" {
            val log = logging("2023-01-23")
            val html = loadPage("2023-01-23.html")
            val scraper = scraper()
            val date = scraper.matchValidity(html)
            val content = scraper.matchContent(html)

            log.i { date }
            log.i { content }

            dateRangeTest(
                date,
                LocalDate(2023, 1, 23),
                LocalDate(2023, 1, 27),
            )

            val (fs, fel) = testDeconstruct(content)

            commonTest(fs, listOf(3, 3, 3, 3, 3))
            @Suppress("SpellCheckingInspection")
            fs[0].dishList[2].run {
                type shouldBe "Hlavní jídlo 2"
                name shouldBe "Kuřecí steak marinovaný na bylinkách; hranolky"
                price shouldBe 128
                ingredients shouldBe listOf(
                    "kuřecí maso",
                    "bylinky",
                    "marináda",
                )
            }

            commonTest(fel, listOf(3, 3, 4, 3, 4))
            @Suppress("SpellCheckingInspection")
            fel[4].dishList[3].run { // is not in a <strong> tag
                type shouldBe "Hlavní jídlo 3"
                name shouldBe "Špagety Aglio Olio"
                price shouldBe 104
                ingredients shouldBe listOf(
                    "špagety",
                    "česnek",
                    "olivový olej",
                    "feferonky",
                    "parmazán",
                )
            }
        }

        "Scrape 2023-01-30" {
            val log = logging("2023-01-30")
            val html = loadPage("2023-01-30.html")
            val scraper = scraper()
            val date = scraper.matchValidity(html)
            val content = scraper.matchContent(html)

            log.i { date }
            log.i { content }

            dateRangeTest(
                date,
                LocalDate(2023, 1, 30),
                LocalDate(2023, 2, 3),
            )

            val (fs, fel) = testDeconstruct(content)

            commonTest(fs, listOf(3, 3, 3, 3, 2))
            @Suppress("SpellCheckingInspection")
            fs[2].dishList[1].run {
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Špíz z vepřového a kuřecího masa, vařené brambory/hranolkyr"
                price shouldBe 128
                ingredients shouldBe listOf(
                    "vepřové a kuřecí maso",
                    "cibule",
                    "slanina",
                    "klobása",
                )
            }

            commonTest(fel, listOf(4, 4, 4, 3, 5))
            @Suppress("SpellCheckingInspection")
            fel[0].dishList[1].run { // is not in a <strong> tag
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Kuřecí směs s nivou; hranolky / rýže"
                price shouldBe 119
                ingredients shouldBe listOf(
                    "kuřecí maso",
                    "niva",
                    "smetana",
                    "směs koření",
                )
            }
        }

        "Scrape 2023-02-20" {
            val log = logging("2023-02-20")
            val html = loadPage("2023-02-20.html")
            val scraper = scraper()
            val date = scraper.matchValidity(html)
            val content = scraper.matchContent(html)

            log.i { date }
            log.i { content }

            dateRangeTest(
                date,
                LocalDate(2023, 2, 20),
                LocalDate(2023, 2, 24),
            )

            val (fs, fel) = testDeconstruct(content)

            commonTest(fs, listOf(3, 4, 3, 3, 2))
            @Suppress("SpellCheckingInspection")
            fs[4].dishList[1].run {
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Grilovaný vepřový kotlet po balkánsku, pikantní zelný salát, hranolky"
                price shouldBe 128
                ingredients shouldBe listOf(
                    "vepřové maso",
                    "směs koření",
                )
            }

            commonTest(fel, listOf(3, 4, 3, 3, 3))
            @Suppress("SpellCheckingInspection")
            fel[3].dishList[1].run { // is not in a <strong> tag
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Marinovaný kuřecí plátek s baby karotkou; vařené brambory / hranolky"
                price shouldBe 119
                ingredients shouldBe listOf(
                    "kuřecí maso",
                    "marináda",
                    "baby karotka",
                )
            }
        }

        "Scrape 2023-02-27" {
            val log = logging("2023-02-27")
            val html = loadPage("2023-02-27.html")
            val scraper = scraper()
            val date = scraper.matchValidity(html)
            val content = scraper.matchContent(html)

            log.i { date }
            log.i { content }

            dateRangeTest(
                date,
                LocalDate(2023, 2, 27),
                LocalDate(2023, 3, 3),
            )

            val (fs, fel) = testDeconstruct(content)

            commonTest(fs, listOf(3, 3, 3, 3, 3))
            @Suppress("SpellCheckingInspection")
            fs[4].dishList[1].run {
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Pikantní kuřecí nudličky; rýže"
                price shouldBe 119
                ingredients shouldBe listOf(
                    "kuřecí maso",
                    "směs koření",
                    "zelenina",
                )
            }

            commonTest(fel, listOf(3, 3, 4, 3, 4))
            @Suppress("SpellCheckingInspection")
            fel[3].dishList[1].run { // is not in a <strong> tag
                type shouldBe "Hlavní jídlo 1"
                name shouldBe "Smažený karbanátek; bramborová kaše"
                price shouldBe 123
                ingredients shouldBe listOf(
                    "mleté maso",
                    "směs koření",
                    "trojobal",
                )
            }
        }

        "Scrape summer" {
            val log = logging("summer")
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

private fun testDeconstruct(content: Outcome<Pair<Option<Nel<DomainError>>, Pair<List<DishDayDto>, List<DishDayDto>>>>): Pair<List<DishDayDto>, List<DishDayDto>> {
    content.shouldBeInstanceOf<Right<Pair<Option<Nel<DomainError>>, Pair<List<DishDayDto>, List<DishDayDto>>>>>()
    content.value.first shouldBe None
    val (fs, fel) = content.value.second
    return fs to fel
}

private fun commonTest(buffet: List<DishDayDto>, days: List<Int>) {
    buffet.map { it.dishList.size } shouldBe days
    // checks if all the html parts were successfully removed
    buffet.map { day ->
        day.dishList.map { listOf(it.name, it.type, it.ingredients.toString()) }
    }.flatten().flatten()
        .forEach {
            it shouldNotContain "<"
            it shouldNotContain ">"
        }
}
