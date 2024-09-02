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

package cz.lastaapps.core.util.datastructures

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.get
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class StateFlowSettings(
    private val settings: Settings,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : ObservableSettings {
    override val keys: Set<String>
        get() = settings.keys

    override val size: Int
        get() = settings.size

    private val map = MutableStateFlow(persistentMapOf<String, Any?>())

    override fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addBooleanOrNullListener(
        key: String,
        callback: (Boolean?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    override fun addDoubleListener(
        key: String,
        defaultValue: Double,
        callback: (Double) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addDoubleOrNullListener(
        key: String,
        callback: (Double?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    override fun addFloatListener(
        key: String,
        defaultValue: Float,
        callback: (Float) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addFloatOrNullListener(
        key: String,
        callback: (Float?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    override fun addIntListener(
        key: String,
        defaultValue: Int,
        callback: (Int) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addIntOrNullListener(
        key: String,
        callback: (Int?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    override fun addLongListener(
        key: String,
        defaultValue: Long,
        callback: (Long) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addLongOrNullListener(
        key: String,
        callback: (Long?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    override fun addStringListener(
        key: String,
        defaultValue: String,
        callback: (String) -> Unit,
    ): SettingsListener = handleDefault(key, defaultValue, callback)

    override fun addStringOrNullListener(
        key: String,
        callback: (String?) -> Unit,
    ): SettingsListener = handleNull(key, callback)

    private inline fun <reified T : Any> handleDefault(
        key: String,
        defaultValue: T,
        crossinline callback: (T) -> Unit,
    ): SettingsListener {
        map.update {
            if (!map.value.containsKey(key)) {
                it.put(key, settings.get<T>(key))
            } else {
                it
            }
        }
        val job =
            map
                .map { m -> m[key] as T? ?: defaultValue }
                .onEach { callback(it) }
                .launchIn(scope)

        return JobListener(job)
    }

    private inline fun <reified T : Any> handleNull(
        key: String,
        crossinline callback: (T?) -> Unit,
    ): SettingsListener {
        map.update {
            if (!map.value.containsKey(key)) {
                it.put(key, settings.get<T>(key))
            } else {
                it
            }
        }
        val job =
            map
                .map { m -> m[key] as T? }
                .onEach { callback(it) }
                .launchIn(scope)

        return JobListener(job)
    }

    class JobListener(
        private val job: Job,
    ) : SettingsListener {
        override fun deactivate() {
            job.cancel()
        }
    }

    override fun clear() {
        settings.clear()
        map.value = persistentMapOf()
    }

    override fun getBoolean(
        key: String,
        defaultValue: Boolean,
    ): Boolean = settings.getBoolean(key, defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? = settings.getBooleanOrNull(key)

    override fun getDouble(
        key: String,
        defaultValue: Double,
    ): Double = settings.getDouble(key, defaultValue)

    override fun getDoubleOrNull(key: String): Double? = settings.getDoubleOrNull(key)

    override fun getFloat(
        key: String,
        defaultValue: Float,
    ): Float = settings.getFloat(key, defaultValue)

    override fun getFloatOrNull(key: String): Float? = settings.getFloatOrNull(key)

    override fun getInt(
        key: String,
        defaultValue: Int,
    ): Int = settings.getInt(key, defaultValue)

    override fun getIntOrNull(key: String): Int? = settings.getIntOrNull(key)

    override fun getLong(
        key: String,
        defaultValue: Long,
    ): Long = settings.getLong(key, defaultValue)

    override fun getLongOrNull(key: String): Long? = settings.getLongOrNull(key)

    override fun getString(
        key: String,
        defaultValue: String,
    ): String = settings.getString(key, defaultValue)

    override fun getStringOrNull(key: String): String? = settings.getStringOrNull(key)

    override fun hasKey(key: String): Boolean = settings.hasKey(key)

    override fun putBoolean(
        key: String,
        value: Boolean,
    ) {
        map.update {
            settings.putBoolean(key, value)
            it.put(key, value)
        }
    }

    override fun putDouble(
        key: String,
        value: Double,
    ) {
        map.update {
            settings.putDouble(key, value)
            it.put(key, value)
        }
    }

    override fun putFloat(
        key: String,
        value: Float,
    ) {
        map.update {
            settings.putFloat(key, value)
            it.put(key, value)
        }
    }

    override fun putInt(
        key: String,
        value: Int,
    ) {
        map.update {
            settings.putInt(key, value)
            it.put(key, value)
        }
    }

    override fun putLong(
        key: String,
        value: Long,
    ) {
        map.update {
            settings.putLong(key, value)
            it.put(key, value)
        }
    }

    override fun putString(
        key: String,
        value: String,
    ) {
        map.update {
            settings.putString(key, value)
            it.put(key, value)
        }
    }

    override fun remove(key: String) {
        map.update { it.remove(key) }
        settings.remove(key)
    }
}
