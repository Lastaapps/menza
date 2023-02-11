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

package cz.lastaapps.menza.features.settings.data.datasource

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
@JvmInline
internal value class GeneralSettings(val settings: FlowSettings) {
    @OptIn(ExperimentalSettingsImplementation::class)
    companion object {
        private val Context.store by preferencesDataStore("menza_general_store")

        fun create(context: Context) = GeneralSettings(DataStoreSettings(context.store))
    }
}

internal interface GeneralDataSource {
    suspend fun storeAppSetupFinished()
    fun isAppSetupFinished(): Flow<Boolean>
}

internal class GeneralDataSourceImpl(
    generalSettings: GeneralSettings,
) : GeneralDataSource {
    private val settings = generalSettings.settings

    companion object {
        private const val appSetupFinishedKey = "app_setup_finished"
    }

    override suspend fun storeAppSetupFinished() {
        settings.putBoolean(appSetupFinishedKey, true)
    }

    override fun isAppSetupFinished(): Flow<Boolean> =
        settings.getBooleanFlow(appSetupFinishedKey, false)
}
