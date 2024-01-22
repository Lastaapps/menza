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
import cz.lastaapps.menza.api.agata.api.SubsystemApiImpl
import cz.lastaapps.menza.api.agata.data.createAgataClient
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.dto.AddressDto
import cz.lastaapps.menza.api.agata.data.model.dto.ContactDto
import cz.lastaapps.menza.api.agata.data.model.dto.InfoDto
import cz.lastaapps.menza.api.agata.data.model.dto.LinkDto
import cz.lastaapps.menza.api.agata.data.model.dto.NewsDto
import cz.lastaapps.menza.api.agata.data.model.dto.OpenTimeDto
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel.BODY
import io.ktor.client.plugins.logging.Logging

class SubsystemApiTest : StringSpec(
    {

        fun client() = createAgataClient(
            HttpClient {
                install(Logging) {
                    level = BODY
                }
            },
            AgataBEConfig.prod,
        )

        fun api() = SubsystemApiImpl(client())

        val ids = listOf(1, 2, 3, 5, 6, 8, 9, 12, 15)

        "getInfo" {
            ids.forEach { subsystemId ->
                val res = api().getInfo(subsystemId)
                res.shouldBeInstanceOf<Right<List<InfoDto>>>()
            }
        }

        "getNews" {
            ids.forEach { subsystemId ->
                val res = api().getNews(subsystemId)
                res.shouldBeInstanceOf<Right<NewsDto>>()
            }
        }

        "getOpeningTimes" {
            ids.forEach { subsystemId ->
                val res = api().getOpeningTimes(subsystemId)
                res.shouldBeInstanceOf<Right<List<OpenTimeDto>>>()
            }
        }

        "getContacts" {
            val res = api().getContacts()
            res.shouldBeInstanceOf<Right<List<ContactDto>>>()
        }

        "getAddress" {
            val res = api().getAddress()
            res.shouldBeInstanceOf<Right<List<AddressDto>>>()
        }

        "getLink" {
            ids.forEach { subsystemId ->
                val res = api().getLink(subsystemId)
                res.shouldBeInstanceOf<Right<List<LinkDto>>>()
            }
        }
    },
)
