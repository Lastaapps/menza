/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.api.agata.api

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.extensions.catchingNetwork
import cz.lastaapps.menza.api.agata.data.AgataClient
import cz.lastaapps.menza.api.agata.data.model.ApiLang
import cz.lastaapps.menza.api.agata.data.model.Func.Dish
import cz.lastaapps.menza.api.agata.data.model.Func.DishHash
import cz.lastaapps.menza.api.agata.data.model.Func.Pictogram
import cz.lastaapps.menza.api.agata.data.model.Func.PictogramHash
import cz.lastaapps.menza.api.agata.data.model.Func.Strahov
import cz.lastaapps.menza.api.agata.data.model.Func.StrahovHash
import cz.lastaapps.menza.api.agata.data.model.Func.Week
import cz.lastaapps.menza.api.agata.data.model.Func.WeekDays
import cz.lastaapps.menza.api.agata.data.model.dto.DishDto
import cz.lastaapps.menza.api.agata.data.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.data.model.dto.StrahovDto
import cz.lastaapps.menza.api.agata.data.model.dto.WeekDishDto
import cz.lastaapps.menza.api.agata.data.model.dto.WeekDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.call.body

internal interface DishApi {
    suspend fun getDishes(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<DishDto>?>

    suspend fun getDishesHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String>

    suspend fun getPictogram(lang: ApiLang): Outcome<List<PictogramDto>?>

    suspend fun getPictogramHash(lang: ApiLang): Outcome<String>

    suspend fun getWeeks(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<WeekDto>?>

    suspend fun getWeekDishList(
        lang: ApiLang,
        weekId: Int,
    ): Outcome<List<WeekDishDto>?>

    suspend fun getStrahov(lang: ApiLang): Outcome<List<StrahovDto>?>

    suspend fun getStrahovHash(lang: ApiLang): Outcome<String>
}

internal class DishApiImpl(
    agataClient: AgataClient,
) : DishApi {
    private val client = agataClient.client

    override suspend fun getDishes(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<DishDto>?> =
        catchingNetwork {
            client.getFun(Dish, lang, subsystemId = subsystemId, secondId = 1).body()
        }

    override suspend fun getDishesHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> =
        catchingNetwork {
            client.getFun(DishHash, lang, subsystemId = subsystemId, secondId = 1).body<String>()
        }

    override suspend fun getPictogram(lang: ApiLang): Outcome<List<PictogramDto>?> =
        catchingNetwork {
            client.getFun(Pictogram, lang).body()
        }

    override suspend fun getPictogramHash(lang: ApiLang): Outcome<String> =
        catchingNetwork {
            client.getFun(PictogramHash, lang).body()
        }

    override suspend fun getWeeks(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<WeekDto>?> =
        catchingNetwork {
            client.getFun(Week, lang, subsystemId = subsystemId).body()
        }

    override suspend fun getWeekDishList(
        lang: ApiLang,
        weekId: Int,
    ): Outcome<List<WeekDishDto>?> =
        catchingNetwork {
            client.getFun(WeekDays, lang, secondId = weekId).body()
        }

    override suspend fun getStrahov(lang: ApiLang): Outcome<List<StrahovDto>?> =
        catchingNetwork {
            client.getFun(Strahov, lang).body()
        }

    override suspend fun getStrahovHash(lang: ApiLang): Outcome<String> =
        catchingNetwork {
            client.getFun(StrahovHash, lang).body()
        }
}
