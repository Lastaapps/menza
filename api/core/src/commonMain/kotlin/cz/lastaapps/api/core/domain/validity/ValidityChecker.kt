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

package cz.lastaapps.api.core.domain.validity

import cz.lastaapps.core.util.extensions.CET
import cz.lastaapps.core.util.extensions.atMidnight
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration

/**
 * Checks if data stored are valid based on date
 */
interface ValidityChecker {
    suspend fun invalidateKey(key: ValidityKey)

    suspend fun onDataUpdated(key: ValidityKey)

    /**
     * Returns true if data was updated recently
     */
    fun isRecent(key: ValidityKey): Flow<Boolean>

    /**
     * Returns true if data were fetched today or later
     */
    fun isFromToday(key: ValidityKey): Flow<Boolean>

    /**
     * Returns true if data was fetched this week or later
     */
    fun isThisWeek(key: ValidityKey): Flow<Boolean>

    /**
     * Returns true if data was updated in the last [duration]
     */
    fun isUpdatedSince(
        key: ValidityKey,
        duration: Duration,
    ): Flow<Boolean>

    /**
     * Checks if the data were updated since the [date] given
     */
    fun isUpdatedSince(
        key: ValidityKey,
        date: Flow<Instant>,
    ): Flow<Boolean>
}

fun ValidityChecker.isUpdatedSince(
    key: ValidityKey,
    date: Flow<LocalDate>,
    zone: TimeZone = TimeZone.CET,
): Flow<Boolean> = isUpdatedSince(key, date.map { it.atMidnight().toInstant(zone) })
