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

package cz.lastaapps.api.core.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.isUpdatedSince
import cz.lastaapps.core.util.extensions.CET
import cz.lastaapps.core.util.extensions.atMidnight
import cz.lastaapps.core.util.extensions.deserializeValueOrNullFlow
import cz.lastaapps.core.util.extensions.findMonday
import cz.lastaapps.core.util.extensions.serializeValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi

@JvmInline
internal value class ValiditySettings(val settings: ObservableSettings)

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
internal class ValidityCheckerImpl(
    private val clock: Clock,
    validitySettings: ValiditySettings,
) : ValidityChecker {

    private val settings = validitySettings.settings

    companion object {
        private const val prefix = "validity_"
        private val recentThreshold = 2.minutes
    }

    private fun key(key: ValidityKey) = prefix + key.name

    override suspend fun onDataUpdated(key: ValidityKey) {
        settings.serializeValue(Instant.serializer(), key(key), clock.now())
    }

    override fun isRecent(key: ValidityKey): Flow<Boolean> =
        isUpdatedSince(key, recentThreshold).distinctUntilChanged()

    override fun isFromToday(key: ValidityKey): Flow<Boolean> {
        val today =
            clock.now().toLocalDateTime(TimeZone.CET).date.atMidnight().toInstant(TimeZone.CET)
        return isUpdatedSince(key, today).distinctUntilChanged()
    }

    override fun isThisWeek(key: ValidityKey): Flow<Boolean> {
        val weekStart = clock.now().toLocalDateTime(TimeZone.CET).date.findMonday()
        return isUpdatedSince(key, weekStart, TimeZone.CET).distinctUntilChanged()
    }

    override fun isUpdatedSince(key: ValidityKey, duration: Duration): Flow<Boolean> =
        isUpdatedSince(key, clock.now() - duration).distinctUntilChanged()

    override fun isUpdatedSince(key: ValidityKey, date: Instant): Flow<Boolean> =
        settings.deserializeValueOrNullFlow(Instant.serializer(), key(key))
            .map { it != null && it >= date }.distinctUntilChanged()
}
