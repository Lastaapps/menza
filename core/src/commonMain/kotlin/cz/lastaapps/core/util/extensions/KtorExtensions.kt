/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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
import co.touchlab.kermit.Logger
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.error.NetworkError
import java.io.IOException
import java.net.SocketException

suspend fun <T> catchingNetwork(block: suspend () -> T): Outcome<T> =
    Either.catch { block() }.mapLeft { exception ->
        Logger.withTag("catchingNetwork").e(exception) { "Failed network call" }

        when (exception::class.simpleName) {
            "TimeoutException",
            "HttpRequestTimeoutException",
            "SocketTimeoutException",
            "OutOfSpaceException", // somehow thrown inside the KTor HttpRequestTimeoutException constructor
            -> NetworkError.Timeout

            "UnknownHostException",
            "NoRouteToHostException",
            "SSLException",
            "SocketException",
            -> NetworkError.NoInternet

            "JsonConvertException",
            "JsonDecodingException",
            -> NetworkError.SerializationError(exception)

            else -> null
        }?.let { return@mapLeft it }

        when (exception) {
//            "ConnectException",
//            "ConnectTimeoutException", // when host in unreachable, but DNS succeeded
            is SocketException,
            -> NetworkError.Unreachable

            is IOException,
            -> NetworkError.NoInternet

            else -> null
        }?.let { return@mapLeft it }

        DomainError.Unknown(exception)
    }
