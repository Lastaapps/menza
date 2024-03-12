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
import cz.lastaapps.menza.api.agata.data.model.Func.ServingPaces
import cz.lastaapps.menza.api.agata.data.model.Func.ServingPacesHash
import cz.lastaapps.menza.api.agata.data.model.Func.Subsystem
import cz.lastaapps.menza.api.agata.data.model.Func.SubsystemHash
import cz.lastaapps.menza.api.agata.data.model.Func.Types
import cz.lastaapps.menza.api.agata.data.model.Func.TypesHash
import cz.lastaapps.menza.api.agata.data.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.data.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.data.model.dto.SubsystemDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.call.body

internal interface CafeteriaApi {

    suspend fun getSubsystems(lang: ApiLang): Outcome<List<SubsystemDto>?>
    suspend fun getSubsystemsHash(lang: ApiLang): Outcome<String>

    suspend fun getServingPlaces(lang: ApiLang, subsystemId: Int): Outcome<List<ServingPlaceDto>?>
    suspend fun getServingPlacesHash(lang: ApiLang, subsystemId: Int): Outcome<String>

    suspend fun getDishTypes(lang: ApiLang, subsystemId: Int): Outcome<List<DishTypeDto>?>
    suspend fun getDishTypesHash(lang: ApiLang, subsystemId: Int): Outcome<String>
}

internal class CafeteriaApiImpl(
    agataClient: AgataClient,
) : CafeteriaApi {
    private val client = agataClient.client

    override suspend fun getSubsystems(
        lang: ApiLang,
    ): Outcome<List<SubsystemDto>?> = catchingNetwork {
        client.getFun(Subsystem, lang, secondId = 1).body()
    }

    override suspend fun getSubsystemsHash(
        lang: ApiLang,
    ): Outcome<String> = catchingNetwork {
        client.getFun(SubsystemHash, lang, secondId = 1).body()
    }

    override suspend fun getServingPlaces(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<ServingPlaceDto>?> = catchingNetwork {
        client.getFun(ServingPaces, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getServingPlacesHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(ServingPacesHash, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getDishTypes(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<DishTypeDto>?> = catchingNetwork {
        client.getFun(Types, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getDishTypesHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(TypesHash, lang, subsystemId = subsystemId).body()
    }
}
