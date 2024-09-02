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

package cz.lastaapps.api.core.domain.sync

import arrow.core.Nel
import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import cz.lastaapps.api.core.domain.sync.SyncResult.Problem
import cz.lastaapps.api.core.domain.sync.SyncResult.Skipped
import cz.lastaapps.api.core.domain.sync.SyncResult.Unavailable
import cz.lastaapps.api.core.domain.sync.SyncResult.Updated
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.SyncError
import cz.lastaapps.core.domain.error.ApiError.WeekNotAvailable
import cz.lastaapps.core.domain.error.DomainError

typealias SyncOutcome = Outcome<SyncResult>

sealed interface SyncResult {
    data object Updated : SyncResult

    data object Skipped : SyncResult

    data object Unavailable : SyncResult

    @JvmInline
    value class Problem(
        val errors: Nel<DomainError>,
    ) : SyncResult
}

fun SyncOutcome.mapSync() =
    map {
        when (it) {
            is Problem -> {
                when (it.errors.first()) {
                    is SyncError.Closed -> SyncError.Closed
                    is WeekNotAvailable -> WeekNotAvailable
                    else -> SyncError.Problem(it.errors)
                }.left()
            }

            Unavailable -> SyncError.Unavailable.left()
            Skipped -> it.right()
            Updated -> it.right()
        }
    }.flatten()
