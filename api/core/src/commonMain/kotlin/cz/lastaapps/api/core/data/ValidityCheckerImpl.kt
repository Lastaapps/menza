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

package cz.lastaapps.api.core.data

import com.russhwolf.settings.ObservableSettings
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.isUpdatedSince
import cz.lastaapps.core.domain.util.InstantSerializer
import cz.lastaapps.core.util.extensions.CET
import cz.lastaapps.core.util.extensions.atMidnight
import cz.lastaapps.core.util.extensions.deserializeValueOrNullFlow
import cz.lastaapps.core.util.extensions.durationTicker
import cz.lastaapps.core.util.extensions.findMonday
import cz.lastaapps.core.util.extensions.serializeValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@JvmInline
internal value class ValiditySettings(
    val settings: ObservableSettings,
)

internal class ValidityCheckerImpl
    constructor(
        private val clock: Clock,
        validitySettings: ValiditySettings,
    ) : ValidityChecker {
        private val settings = validitySettings.settings
        private val timeZone = TimeZone.CET

        companion object {
            private const val PREFIX = "validity_"
            private val recentThreshold = 2.minutes
        }

        private fun key(key: ValidityKey) = PREFIX + key.name

        override suspend fun invalidateKey(key: ValidityKey) {
            settings.remove(key(key))
        }

        override suspend fun onDataUpdated(key: ValidityKey) {
            settings.serializeValue(InstantSerializer, key(key), clock.now())
        }

        override fun isRecent(key: ValidityKey): Flow<Boolean> = isUpdatedSince(key, recentThreshold).distinctUntilChanged()

        override fun isFromToday(key: ValidityKey): Flow<Boolean> {
            val today =
                clock.durationTicker().map {
                    it
                        .toLocalDateTime(timeZone)
                        .date
                        .atMidnight()
                        .toInstant(timeZone)
                }
            return isUpdatedSince(key, today).distinctUntilChanged()
        }

        override fun isThisWeek(key: ValidityKey): Flow<Boolean> {
            val weekStart =
                clock.durationTicker().map {
                    it
                        .toLocalDateTime(timeZone)
                        .date
                        .findMonday()
                }
            return isUpdatedSince(key, weekStart, timeZone).distinctUntilChanged()
        }

        override fun isUpdatedSince(
            key: ValidityKey,
            duration: Duration,
        ): Flow<Boolean> =
            isUpdatedSince(
                key,
                clock.durationTicker().map { it - duration },
            ).distinctUntilChanged()

        override fun isUpdatedSince(
            key: ValidityKey,
            date: Flow<Instant>,
        ): Flow<Boolean> =
            combine(
                settings
                    .deserializeValueOrNullFlow(InstantSerializer, key(key))
                    .distinctUntilChanged(),
                date.distinctUntilChanged(),
            ) { saved, date ->
                saved != null && saved >= date
            }.distinctUntilChanged()
    }
