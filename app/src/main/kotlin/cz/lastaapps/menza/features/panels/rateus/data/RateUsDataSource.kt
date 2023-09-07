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

package cz.lastaapps.menza.features.panels.rateus.data

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

@OptIn(
    ExperimentalSettingsApi::class,
    ExperimentalSettingsImplementation::class,
)
@JvmInline
internal value class RateUsStore(val settings: FlowSettings) {
    companion object {
        private val Context.store by preferencesDataStore("menza_rate_us_store")

        fun create(context: Context) = RateUsStore(DataStoreSettings(context.store))
    }
}

internal interface RateUsDataSource {
    fun getShouldRate(): Flow<Instant?>
    suspend fun setShouldRate(instant: Instant)
    fun isDisabled(): Flow<Boolean?>
    suspend fun setDisabled(notAllowed: Boolean)
}

@ExperimentalSettingsApi
internal class RateUsDataSourceImpl(
    rateUsStore: RateUsStore,
) : RateUsDataSource {
    private val store = rateUsStore.settings

    override fun getShouldRate(): Flow<Instant?> =
        store.getLongOrNullFlow(KEY_LAST_OPENED).map {
            it?.let { Instant.fromEpochSeconds(it) }
        }

    override suspend fun setShouldRate(instant: Instant) {
        store.putLong(KEY_LAST_OPENED, instant.epochSeconds)
    }

    override fun isDisabled(): Flow<Boolean?> =
        store.getBooleanOrNullFlow(KEY_DISABLED)

    override suspend fun setDisabled(notAllowed: Boolean) =
        store.putBoolean(KEY_DISABLED, notAllowed)

    companion object {
        private const val KEY_LAST_OPENED = "last_opened"
        private const val KEY_DISABLED = "disabled"
    }
}
