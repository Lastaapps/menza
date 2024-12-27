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

package cz.lastaapps.api.rating.api.model

import io.ktor.http.URLProtocol

internal data class RatingBEConfig(
    val host: String,
    val protocol: URLProtocol,
    val path: String,
    val apiKey: String,
) {
    companion object {
        val prod =
            RatingBEConfig(
                host = "lastope2.sh.cvut.cz",
                protocol = URLProtocol.HTTPS,
                path = "menza/api/",
                apiKey = "amnmDMRMoMimdm_MDMBNdNDn3neNCNbN-U4UCUaUBU-U4U0U9U7U-TaTcT0TbT-P5P9P0P6P1P8PBPeP0PcP8PdP".deobfuscate(),
            )

        private fun String.deobfuscate() =
            map { it.lowercase()[0] }
                .filterIndexed { ind, _ -> ind % 2 == 0 }
                .joinToString("")
    }
}
