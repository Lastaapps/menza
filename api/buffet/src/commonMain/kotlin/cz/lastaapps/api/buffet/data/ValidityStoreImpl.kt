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

package cz.lastaapps.api.buffet.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.decodeValue
import com.russhwolf.settings.serialization.encodeValue
import cz.lastaapps.api.buffet.domain.ValidityStore
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class, ExperimentalSettingsApi::class)
internal class ValidityStoreImpl(
    private val settings: Settings,
    private val clock: Clock,
) : ValidityStore {
    companion object {
        private const val untilKey = "buffet_until"
        private val timeZone = TimeZone.of("Europe/Prague")
    }

    override suspend fun shouldReload(): Boolean {
        val stored = settings.decodeValue(LocalDate.serializer(), untilKey, LocalDate(2023, 1, 1))
        val now = clock.now().toLocalDateTime(timeZone).date
        return stored < now
    }

    override suspend fun storeValidUntil(until: LocalDate) {
        settings.encodeValue(LocalDate.serializer(), untilKey, until)
    }
}
