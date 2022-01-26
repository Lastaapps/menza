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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.StateFlow

sealed class DarkMode private constructor(val id: Int) {
    object Light : DarkMode(0)
    object Dark : DarkMode(1)
    object System : DarkMode(2)

    companion object {
        val modes = listOf(Light, Dark, System)
    }
}

private val darkModeKey = intPreferencesKey("darkMode")
val SettingsStore.darkMode: StateFlow<DarkMode>
    get() = data.mapState { pref ->
        val key = pref[darkModeKey]
        DarkMode.modes.firstOrNull { it.id == key } ?: DarkMode.System
    }

suspend fun SettingsStore.setDarkMode(darkMode: DarkMode) {
    edit {
        it[darkModeKey] = darkMode.id
    }
}

@Composable
fun DarkMode.resolveShouldUseDark(): Boolean {
    return when (this) {
        DarkMode.Dark -> true
        DarkMode.Light -> false
        DarkMode.System -> isSystemInDarkTheme()
    }
}