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

package cz.lastaapps.api.agata.test

import arrow.core.Either.Right
import cz.lastaapps.core.util.doAFuckingSetupForTestBecauseThisShitIsNiceButBroken
import cz.lastaapps.menza.api.agata.api.DishApiImpl
import cz.lastaapps.menza.api.agata.data.createAgataClient
import cz.lastaapps.menza.api.agata.data.model.dto.DishDto
import cz.lastaapps.menza.api.agata.data.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.data.model.dto.StrahovDto
import cz.lastaapps.menza.api.agata.data.model.dto.WeekDishDto
import cz.lastaapps.menza.api.agata.data.model.dto.WeekDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel.BODY
import io.ktor.client.plugins.logging.Logging
import org.lighthousegames.logging.KmLogging

class DishApiTest : StringSpec({

    KmLogging.doAFuckingSetupForTestBecauseThisShitIsNiceButBroken()

    fun client() = createAgataClient(HttpClient() {
        install(Logging) {
            level = BODY
        }
    })

    fun api() = DishApiImpl(client())

    val ids = listOf(1, 2, 3, 5, 6, 8, 9, 12, 15)
    val weekIds = listOf(1, 2, 3, 6, 8, 9)

    "getDishes" {
        ids.forEach { subsystemId ->
            val res = api().getDishes(subsystemId)
            res.shouldBeInstanceOf<Right<List<DishDto>>>()
        }
    }
    "getPictogram" {
        val res = api().getPictogram()
        res.shouldBeInstanceOf<Right<List<PictogramDto>>>()
    }
    "getWeeksValid" {
        weekIds.forEach { subsystemId ->
            val res = api().getWeeks(subsystemId)
            res.shouldBeInstanceOf<Right<List<WeekDto>>>()
            res.value.shouldNotBeEmpty()
            res.value.map { it.id }.forEach { id ->
                val res2 = api().getWeekDishList(id)
                res2.shouldBeInstanceOf<Right<List<WeekDishDto>>>()
            }
        }
        (ids - weekIds).forEach { subsystemId ->
            val res = api().getWeeks(subsystemId)
            res.shouldBeInstanceOf<Right<List<WeekDto>?>>()
            res.value shouldBe null
        }
    }
    "getStrahov" {
        val res = api().getStrahov()
        res.shouldBeInstanceOf<Right<List<StrahovDto>>>()
    }
})
