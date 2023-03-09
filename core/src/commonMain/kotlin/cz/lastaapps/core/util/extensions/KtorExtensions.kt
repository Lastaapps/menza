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

package cz.lastaapps.core.util.extensions

import arrow.core.Either
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.NetworkError
import org.lighthousegames.logging.logging

suspend fun <T> catchingNetwork(block: suspend () -> T): Outcome<T> =
    Either.catch { block() }.mapLeft {
        logging("catchingNetwork").e(it) { "Failed network call" }

        when (it::class.simpleName) {
            "TimeoutException",
            -> NetworkError.Timeout

            "ConnectException",
            -> NetworkError.ConnectionClosed

            "UnknownHostException",
            "NoRouteToHostException",
            "IOException",
            "SSLException",
            "SocketException",
            "HttpRequestTimeoutException",
            "SocketTimeoutException",
            -> NetworkError.NoInternet

            "JsonConvertException",
            "JsonDecodingException",
            -> NetworkError.SerializationError(it)

            else -> MenzaError.Unknown(it)
        }
    }
