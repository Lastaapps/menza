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

package cz.lastaapps.api.agata.test

import arrow.core.Either.Right
import cz.lastaapps.menza.api.agata.api.CafeteriaApiImpl
import cz.lastaapps.menza.api.agata.api.StravnikWalletApiImpl
import cz.lastaapps.menza.api.agata.data.createAgataClient
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.data.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.data.model.dto.SubsystemDto
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel.BODY
import io.ktor.client.plugins.logging.Logging

class StravnikWalletTest : StringSpec(
    {
        fun api() = StravnikWalletApiImpl(
            HttpClient {
                install(Logging) {
                    level = BODY
                }
            },
        )

        "getBalance" {
            val username = ""
            val password = ""
            api()
                .getBalance(username, password)
                .shouldBeRight()
        }
    },
)
