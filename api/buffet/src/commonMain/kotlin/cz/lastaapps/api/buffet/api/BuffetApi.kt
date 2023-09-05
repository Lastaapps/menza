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

package cz.lastaapps.api.buffet.api

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Ior
import arrow.core.None
import arrow.core.Some
import arrow.core.leftIor
import arrow.core.nel
import arrow.core.rightIor
import cz.lastaapps.api.buffet.data.model.WebContentDto
import cz.lastaapps.core.domain.OutcomeIor
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.extensions.catchingNetwork
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

internal interface BuffetApi {
    suspend fun process(): OutcomeIor<WebContentDto>
}

internal class BuffetApiImpl(
    private val client: HttpClient,
    private val scraper: BuffetScraper,
) : BuffetApi {
    override suspend fun process(): OutcomeIor<WebContentDto> = outcome {
        val text = catchingNetwork { getPageText() }.bind()
        val (from, to) = scraper.matchValidity(text).bind()
        val (errors, buffets) = scraper.matchContent(text).bind()
        val (fs, fel) = buffets

        val res = WebContentDto(
            from = from,
            to = to,
            fs = fs,
            fel = fel,
        )

        when (errors) {
            None -> res.rightIor()
            is Some -> Ior.Both(errors.value, res)
        }
    }.let {
        when (it) {
            is Left -> return it.value.nel().leftIor()
            is Right -> it.value
        }
    }

    private suspend fun getPageText() =
        client
            .get("https://studentcatering.cz/jidelni-listek/")
            .bodyAsText()
            .replace("&#8222;", "„")
            .replace("&#8220;", "“")
}
