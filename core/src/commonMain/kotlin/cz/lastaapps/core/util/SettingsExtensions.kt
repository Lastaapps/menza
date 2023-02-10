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

package cz.lastaapps.core.util

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json


inline fun <reified T> Settings.serializeValue(
    serializer: KSerializer<T>,
    key: String,
    value: T,
) = set(key, Json.encodeToString(serializer, value))

inline fun <reified T> Settings.deserializeValue(
    serializer: KSerializer<T>,
    key: String,
    default: T,
) = deserializeValueOrNull(serializer, key) ?: default

inline fun <reified T> Settings.deserializeValueOrNull(
    serializer: KSerializer<T>,
    key: String,
) = getStringOrNull(key)?.let {
    Json.decodeFromString(serializer, it)
}

inline fun <reified T> ObservableSettings.deserializeValueFlow(
    serializer: KSerializer<T>,
    key: String,
    default: T,
) = deserializeValueOrNullFlow(serializer, key).map { it ?: default }

@OptIn(ExperimentalSettingsApi::class)
inline fun <reified T> ObservableSettings.deserializeValueOrNullFlow(
    serializer: KSerializer<T>,
    key: String,
) = getStringOrNullFlow(key).map {
    it?.let {
        Json.decodeFromString(serializer, it)
    }
}

@OptIn(ExperimentalSettingsApi::class)
suspend inline fun <reified T> SuspendSettings.serializeValue(
    serializer: KSerializer<T>,
    key: String,
    value: T,
) = putString(key, Json.encodeToString(serializer, value))

@OptIn(ExperimentalSettingsApi::class)
suspend inline fun <reified T> SuspendSettings.deserializeValue(
    serializer: KSerializer<T>,
    key: String,
    default: T,
) = deserializeValueOrNull(serializer, key) ?: default

@OptIn(ExperimentalSettingsApi::class)
suspend inline fun <reified T> SuspendSettings.deserializeValueOrNull(
    serializer: KSerializer<T>,
    key: String,
) = getStringOrNull(key)?.let {
    Json.decodeFromString(serializer, it)
}

@OptIn(ExperimentalSettingsApi::class)
inline fun <reified T> FlowSettings.deserializeValueFlow(
    serializer: KSerializer<T>,
    key: String,
    default: T,
) = deserializeValueOrNullFlow(serializer, key).map { it ?: default }

@OptIn(ExperimentalSettingsApi::class)
inline fun <reified T> FlowSettings.deserializeValueOrNullFlow(
    serializer: KSerializer<T>,
    key: String,
) = getStringOrNullFlow(key).map {
    it?.let {
        Json.decodeFromString(serializer, it)
    }
}
