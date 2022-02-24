/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

import io.ktor.client.network.sockets.*
import java.io.EOFException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.nio.channels.UnresolvedAddressException

sealed class MenzaError private constructor(
    val isReportable: Boolean,
    val showMessage: Boolean,
    val throwable: Throwable?,
) {
    object WeekNotSupported : MenzaError(false, false, null)

    class Timeout(throwable: Throwable) : MenzaError(false, false, throwable)
    class NoInternet(throwable: Throwable) : MenzaError(false, false, throwable)
    class FailedToConnect(throwable: Throwable) : MenzaError(true, true, throwable)
    class ConnectionClosed(throwable: Throwable) : MenzaError(false, false, throwable)
    class UnknownConnectionError(throwable: Throwable) : MenzaError(true, true, throwable)
    class ParsingError(throwable: Throwable) : MenzaError(true, true, throwable)
}

fun Throwable.toMenzaError(): MenzaError {
    return when (this) {
        is UnresolvedAddressException -> MenzaError.NoInternet(this)
        is ConnectException -> MenzaError.FailedToConnect(this)
        is SocketTimeoutException -> MenzaError.Timeout(this)
        is ConnectTimeoutException -> MenzaError.Timeout(this)
        is SocketException -> MenzaError.FailedToConnect(this)
        is EOFException -> MenzaError.ConnectionClosed(this)
        else -> MenzaError.UnknownConnectionError(this)
    }
}