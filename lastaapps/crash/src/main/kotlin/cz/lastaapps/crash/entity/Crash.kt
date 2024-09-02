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

package cz.lastaapps.crash.entity

import java.time.ZonedDateTime

data class Crash(
    val date: ZonedDateTime,
    val severity: ErrorSeverity,
    val message: String?,
    val trace: String,
    val reported: ReportState,
) {
    companion object {
        fun fromError(
            error: Throwable,
            severity: ErrorSeverity,
        ): Crash =
            Crash(
                date = ZonedDateTime.now(),
                severity = severity,
                message = error.message,
                trace = error.stackTraceToString(),
                reported = ReportState.UNREPORTED,
            )
    }
}
