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

package cz.lastaapps.menza.api.agata.api

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.catchingNetwork
import cz.lastaapps.menza.api.agata.domain.model.Func
import cz.lastaapps.menza.api.agata.domain.model.Func.Dish
import cz.lastaapps.menza.api.agata.domain.model.Func.DishHash
import cz.lastaapps.menza.api.agata.domain.model.Func.Pictogram
import cz.lastaapps.menza.api.agata.domain.model.Func.PictogramHash
import cz.lastaapps.menza.api.agata.domain.model.Func.Week
import cz.lastaapps.menza.api.agata.domain.model.Func.WeekDays
import cz.lastaapps.menza.api.agata.domain.model.dto.DishDto
import cz.lastaapps.menza.api.agata.domain.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.domain.model.dto.StrahovDto
import cz.lastaapps.menza.api.agata.domain.model.dto.WeekDishDto
import cz.lastaapps.menza.api.agata.domain.model.dto.WeekDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.HttpClient
import io.ktor.client.call.body

internal interface DishApi {

    suspend fun getDishes(subsystemId: Int): Outcome<List<DishDto>>
    suspend fun getDishesHash(subsystemId: Int): Outcome<String>

    suspend fun getPictogram(): Outcome<List<PictogramDto>>
    suspend fun getPictogramHash(): Outcome<String>

    suspend fun getWeeks(subsystemId: Int): Outcome<List<WeekDto>>
    suspend fun getWeekDishList(weekId: Int): Outcome<List<WeekDishDto>>

    suspend fun getStrahov(): Outcome<List<StrahovDto>>
}

internal class DishApiImpl(
    private val client: HttpClient,
) : DishApi {
    override suspend fun getDishes(subsystemId: Int): Outcome<List<DishDto>> = catchingNetwork {
        client.getFun(Dish, subsystemId = subsystemId).body()
    }

    override suspend fun getDishesHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(DishHash, subsystemId = subsystemId).body()
    }

    override suspend fun getPictogram(): Outcome<List<PictogramDto>> = catchingNetwork {
        client.getFun(Pictogram).body()
    }

    override suspend fun getPictogramHash(): Outcome<String> = catchingNetwork {
        client.getFun(PictogramHash).body()
    }

    override suspend fun getWeeks(subsystemId: Int): Outcome<List<WeekDto>> = catchingNetwork {
        client.getFun(Week).body()
    }

    override suspend fun getWeekDishList(weekId: Int): Outcome<List<WeekDishDto>> =
        catchingNetwork {
            client.getFun(WeekDays).body()
        }

    override suspend fun getStrahov(): Outcome<List<StrahovDto>> = catchingNetwork {
        client.getFun(Func.Strahov).body()
    }
}
