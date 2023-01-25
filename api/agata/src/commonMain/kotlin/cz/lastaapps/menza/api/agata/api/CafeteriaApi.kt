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
import cz.lastaapps.menza.api.agata.domain.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.domain.model.dto.MenuDto
import cz.lastaapps.menza.api.agata.domain.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.domain.model.dto.SubsystemDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.HttpClient
import io.ktor.client.call.body

internal interface CafeteriaApi {

    suspend fun getMenus(): Outcome<List<MenuDto>>
    suspend fun getMenuHash(): Outcome<String>

    suspend fun getSubsystems(): Outcome<List<SubsystemDto>>
    suspend fun getAllSubsystems(): Outcome<List<SubsystemDto>>
    suspend fun getSubsystemsHash(): Outcome<String>

    suspend fun getServingPlaces(subsystemId: Int): Outcome<List<ServingPlaceDto>>
    suspend fun getServingPlacesHash(subsystemId: Int): Outcome<String>

    suspend fun getDishTypes(subsystemId: Int): Outcome<List<DishTypeDto>>
    suspend fun getDishTypesHash(subsystemId: Int): Outcome<String>
}

internal class CafeteriaApiImpl(
    private val client: HttpClient,
) : CafeteriaApi {
    override suspend fun getMenus(): Outcome<List<MenuDto>> = catchingNetwork {
        client.getFun(Func.Menu).body()
    }

    override suspend fun getMenuHash(): Outcome<String> = catchingNetwork {
        client.getFun(Func.MenuHash).body()
    }

    override suspend fun getSubsystems(): Outcome<List<SubsystemDto>> = catchingNetwork {
        client.getFun(Func.Subsystem).body()
    }

    override suspend fun getAllSubsystems(): Outcome<List<SubsystemDto>> = catchingNetwork {
        client.getFun(Func.SubsystemAll).body()
    }

    override suspend fun getSubsystemsHash(): Outcome<String> = catchingNetwork {
        client.getFun(Func.SubsystemHash).body()
    }

    override suspend fun getServingPlaces(subsystemId: Int): Outcome<List<ServingPlaceDto>> =
        catchingNetwork {
            client.getFun(Func.ServingPaces, subsystemId = subsystemId).body()
        }

    override suspend fun getServingPlacesHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(Func.ServingPacesHash, subsystemId = subsystemId).body()
    }

    override suspend fun getDishTypes(subsystemId: Int): Outcome<List<DishTypeDto>> =
        catchingNetwork {
            client.getFun(Func.Types, subsystemId = subsystemId).body()
        }

    override suspend fun getDishTypesHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(Func.TypesHash, subsystemId = subsystemId).body()
    }
}
