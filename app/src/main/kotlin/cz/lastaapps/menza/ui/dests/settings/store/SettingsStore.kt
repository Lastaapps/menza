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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class SettingsStore(
    appContext: Context,
    private val coroutineScope: CoroutineScope,
) {

    companion object {
        private const val storeName = "settings"
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = storeName)
    }

    val isReady = MutableStateFlow(false)
    private val store = appContext.dataStore

    val data: StateFlow<Preferences>
        get() = mData
    private lateinit var mData: StateFlow<Preferences>

    init {
        coroutineScope.launch {
            mData = store.data.stateIn(this)
            isReady.emit(true)
            store.edit { }
        }
    }

    suspend fun edit(
        transform: suspend (MutablePreferences) -> Unit
    ): Preferences = store.edit(transform)

    fun <T, R> StateFlow<T>.mapState(
        transform: (T) -> R,
    ) = mapState(coroutineScope, transform)
}

inline fun <T, R> StateFlow<T>.mapState(
    scope: CoroutineScope,
    crossinline transform: (T) -> R,
): StateFlow<R> =
    map(transform).stateIn(scope, SharingStarted.WhileSubscribed(), transform(value))