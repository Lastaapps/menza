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

package cz.lastaapps.menza.api.agata.util

import cz.lastaapps.menza.api.agata.data.model.ApiLang
import cz.lastaapps.menza.api.agata.data.model.Func
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal suspend fun HttpClient.getFun(
    func: Func,
    subsystemId: Int? = null,
    secondId: Int? = null,
    lang: ApiLang? = null,
) = get {
    parameter("Funkce", func.funName)
    subsystemId?.let {
        parameter("Podsystem", it)
    }
    secondId?.let {
        parameter("SecondID", it)
    }
    lang?.let {
        parameter("Lang", it.value)
    }
}
