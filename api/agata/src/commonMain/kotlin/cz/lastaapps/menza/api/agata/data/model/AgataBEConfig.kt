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

package cz.lastaapps.menza.api.agata.data.model

import io.ktor.http.URLProtocol

internal data class AgataBEConfig(
    val protocol: URLProtocol,
    val host: String,
    val apiPath: String,
    val apiKey: String,
) {
    @Suppress("SpellCheckingInspection")
    fun photoLinkForAgataSubsystem(subsystemId: Int, name: String) =
        // Use showfotoG for little smaller, but slower responses
        "${protocol.name}://$host/jidelnicky/showfoto.php?clPodsystem=$subsystemId&xFile=$name"

    @Suppress("SpellCheckingInspection")
    fun photoLinkForStrahov(name: String) =
        // Use showfotoMG for smaller, but slower responses
        "${protocol.name}://$host/jidelnicky/showfotoM.php?xFile=$name"

    @Suppress("unused")
    companion object {
        @Suppress("SpellCheckingInspection")
        val prod = AgataBEConfig(
            protocol = URLProtocol.HTTPS,
            host = "agata.suz.cvut.cz",
            apiPath = "jidelnicky/JAPIV2/json_API.php",
            apiKey = "vl1dXKi1WojTvIDb".decrypt(),
        )

        @Suppress("SpellCheckingInspection")
        val staging = AgataBEConfig(
            protocol = URLProtocol.HTTPS,
            host = "agata-new.suz.cvut.cz",
            apiPath = "jidelnicky/JAPIV2/json_API.php",
            apiKey = "vl1dXKi1WojTvIDb".decrypt(),
        )

        // "Decrypt" the key so GitHub scrapers cannot find the real one that easily
        private fun String.decrypt() =
            filterIndexed { index, _ -> index % 2 == 0 }
    }
}
