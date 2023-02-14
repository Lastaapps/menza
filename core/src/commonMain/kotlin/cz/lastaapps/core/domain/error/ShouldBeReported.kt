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

package cz.lastaapps.core.domain.error

import cz.lastaapps.core.domain.error.CommonError.WorkTimeout
import cz.lastaapps.core.domain.error.NetworkError.ConnectionClosed
import cz.lastaapps.core.domain.error.NetworkError.NoInternet
import cz.lastaapps.core.domain.error.NetworkError.SerializationError
import cz.lastaapps.core.domain.error.NetworkError.Timeout

val MenzaError.shouldBeReported: Boolean
    get() = when (this) {
        is MenzaError.Logic -> false

        is MenzaError.Unknown -> true
        is NetworkError -> shouldBeReported
        is CommonError -> shouldBeReported
        is ParsingError -> true
    }

val NetworkError.shouldBeReported: Boolean
    get() = when (this) {
        ConnectionClosed,
        NoInternet,
        Timeout,
        -> false

        is SerializationError,
        -> true
    }

val CommonError.shouldBeReported: Boolean
    get() = when (this) {
        is WorkTimeout -> false
    }
