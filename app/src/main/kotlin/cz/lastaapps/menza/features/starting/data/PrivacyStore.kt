/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.starting.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlin.time.Instant

class PrivacyStore(
    appContext: Context,
) {
    companion object {
        private const val STORE_NAME = "privacy_store"
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = STORE_NAME)
    }

    private val store = appContext.dataStore

    private val approvedKey = stringPreferencesKey("approved")

    val approved: Flow<Instant?>
        get() =
            store.data.map { pref ->
                pref[approvedKey]?.let {
                    Json.decodeFromString<Instant>(it)
                }
            }

    suspend fun setApproved(date: Instant) {
        store.edit {
            it[approvedKey] = Json.encodeToString<Instant>(date)
        }
    }
}
