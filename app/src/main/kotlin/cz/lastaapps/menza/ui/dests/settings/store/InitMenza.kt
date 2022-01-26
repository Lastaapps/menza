/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.settings.store

import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.entity.menza.MenzaId
import kotlinx.coroutines.flow.StateFlow

sealed class InitMenza private constructor(val id: Int) {
    object Ask : InitMenza(0)
    object Remember : InitMenza(1)
    object Specific : InitMenza(2)
}

private val menzaModeKey = intPreferencesKey("menzaMode")
private val menzaPreferredKey = intPreferencesKey("menzaPreferred")
private val menzaLatestKey = intPreferencesKey("menzaLatest")

val SettingsStore.initMenza: StateFlow<InitMenza>
    get() = data.mapState { pref ->
        when (pref[menzaModeKey] ?: 0) {
            1 -> InitMenza.Remember
            2 -> InitMenza.Specific
            else -> InitMenza.Ask
        }
    }

suspend fun SettingsStore.setInitMenza(mode: InitMenza) {
    edit { pref ->
        pref[menzaModeKey] = mode.id
    }
}


val SettingsStore.preferredMenza: StateFlow<MenzaId?>
    get() = data.mapState { pref -> pref[menzaPreferredKey]?.let { MenzaId(it) } }

suspend fun SettingsStore.setPreferredMenza(menzaId: MenzaId) {
    edit { pref ->
        pref[menzaPreferredKey] = menzaId.id
    }
}


val SettingsStore.latestMenza: StateFlow<MenzaId?>
    get() = data.mapState { pref -> pref[menzaLatestKey]?.let { MenzaId(it) } }

suspend fun SettingsStore.setLatestMenza(menzaId: MenzaId) {
    edit { pref ->
        pref[menzaLatestKey] = menzaId.id
    }
}

