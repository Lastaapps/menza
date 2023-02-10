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
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.features.settings.domain.model.InitialMenza
import kotlinx.coroutines.flow.StateFlow

private val menzaModeKey = intPreferencesKey("menzaMode")
private val menzaPreferredKey = intPreferencesKey("menzaPreferred")
private val menzaLatestKey = intPreferencesKey("menzaLatest")

internal val SettingsStore.initialMenza: StateFlow<InitialMenza>
    get() = data.mapState { pref ->
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


internal val SettingsStore.preferredMenza: StateFlow<MenzaId?>
    get() = data.mapState { pref -> pref[menzaPreferredKey]?.let { MenzaId(it) } }

internal suspend fun SettingsStore.setPreferredMenza(menzaId: MenzaId) {
    edit { pref ->
        pref[menzaPreferredKey] = menzaId.id
    }
}


internal val SettingsStore.latestMenza: StateFlow<MenzaId?>
    get() = data.mapState { pref -> pref[menzaLatestKey]?.let { MenzaId(it) } }

internal suspend fun SettingsStore.setLatestMenza(menzaId: MenzaId) {
    edit { pref ->
        pref[menzaLatestKey] = menzaId.id
    }
}

