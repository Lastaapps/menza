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

package cz.lastaapps.menza.features.settings.data

import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.menza.features.settings.domain.model.InitialMenza
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val menzaModeKey = intPreferencesKey("menza_mode")

internal val SettingsStore.initialMenza: Flow<InitialMenza>
    get() = data.map { pref ->
        when (pref[menzaModeKey] ?: 0) {
            1 -> InitialMenza.Remember
            2 -> InitialMenza.Specific
            else -> InitialMenza.Ask
        }
    }

internal suspend fun SettingsStore.setInitialMenza(mode: InitialMenza) {
    edit { pref ->
        pref[menzaModeKey] = mode.id
    }
}
