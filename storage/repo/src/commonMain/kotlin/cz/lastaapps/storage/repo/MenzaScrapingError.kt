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

package cz.lastaapps.storage.repo

import io.ktor.client.network.sockets.ConnectTimeoutException
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.nio.channels.UnresolvedAddressException

sealed class MenzaScrapingError private constructor(
    val isReportable: Boolean,
    val showMessage: Boolean,
    val throwable: Throwable?,
) {
    data object WeekNotSupported : MenzaScrapingError(false, false, null)

    class Timeout(throwable: Throwable) : MenzaScrapingError(false, false, throwable)
    class NoInternet(throwable: Throwable) : MenzaScrapingError(false, false, throwable)
    class FailedToConnect(throwable: Throwable) : MenzaScrapingError(true, true, throwable)
    class ConnectionClosed(throwable: Throwable) : MenzaScrapingError(false, false, throwable)
    class UnknownConnectionError(throwable: Throwable) : MenzaScrapingError(true, true, throwable)
    class ParsingError(throwable: Throwable) : MenzaScrapingError(true, true, throwable)
}

fun Throwable.toMenzaError(): MenzaScrapingError {
    return when (this) {
        is UnresolvedAddressException -> MenzaScrapingError.NoInternet(this)
        is ConnectTimeoutException -> MenzaScrapingError.Timeout(this)
        is ConnectException -> MenzaScrapingError.FailedToConnect(this)
        is SocketTimeoutException -> MenzaScrapingError.Timeout(this)
        is SocketException -> MenzaScrapingError.FailedToConnect(this)
        is EOFException -> MenzaScrapingError.ConnectionClosed(this)
        else -> MenzaScrapingError.UnknownConnectionError(this)
    }
}
