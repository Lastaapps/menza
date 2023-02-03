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

package cz.lastaapps.api.core.domain.validity

import cz.lastaapps.core.util.CET
import cz.lastaapps.core.util.atMidnight
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/**
 * Checks if data stored are valid based on date
 */
interface ValidityChecker {
    suspend fun onDataUpdated(key: ValidityKey)
    fun isRecent(key: ValidityKey): Flow<Boolean>
    fun isFromToday(key: ValidityKey): Flow<Boolean>
    fun isThisWeek(key: ValidityKey): Flow<Boolean>
    fun isUpdatedSince(key: ValidityKey, duration: Duration): Flow<Boolean>
    fun isUpdatedSince(key: ValidityKey, date: Instant): Flow<Boolean>
}

fun ValidityChecker.isUpdatedSince(
    key: ValidityKey,
    date: LocalDate,
    zone: TimeZone = TimeZone.CET,
): Flow<Boolean> = isUpdatedSince(key, date.atMidnight().toInstant(zone))
