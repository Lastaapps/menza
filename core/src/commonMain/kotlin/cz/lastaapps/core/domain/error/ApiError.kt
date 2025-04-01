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

package cz.lastaapps.core.domain.error

import arrow.core.Nel

// Used for display purposes only
sealed interface ApiError : DomainError.Logic {
    data object WeekNotAvailable : ApiError

    sealed interface SyncError : ApiError {
        data object Unavailable : SyncError

        data object Closed : SyncError

        @JvmInline
        value class Problem(
            val errors: Nel<DomainError>,
        ) : SyncError
    }

    sealed interface WalletError : ApiError {
        @JvmInline
        value class TotallyBroken(
            override val extraMessage: String? = null,
        ) : WalletError

        data object InvalidCredentials : WalletError

        data object Unavailable : WalletError
    }

    sealed interface RatingError : ApiError {
        data object Unauthorized : RatingError

        data class TooManyRequests(
            val reason: String,
        ) : RatingError

        data class OldAppVersion(
            val reason: String,
        ) : RatingError

        data class OtherProblem(
            val code: Int,
        ) : RatingError

        data class Unrelated(
            val cause: DomainError,
        ) : RatingError {
            override val throwable: Throwable?
                get() = cause.throwable
        }

        companion object {
            fun wrap(cause: DomainError): RatingError {
                if (cause is RatingError) {
                    return cause
                }
                return Unrelated(cause)
            }
        }
    }
}
