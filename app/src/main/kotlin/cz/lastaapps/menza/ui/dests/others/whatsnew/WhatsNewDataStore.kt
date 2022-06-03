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

package cz.lastaapps.menza.ui.dests.others.whatsnew

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cz.lastaapps.menza.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WhatsNewDataStore(context: Context) {
    companion object {
        private val Context.whatsNewDataStore by preferencesDataStore("whatsnew_data_store")
        private val lastViewedKey = intPreferencesKey("last_viewed")
    }

    private val store = context.whatsNewDataStore

    val lastViewed: Flow<Int> = store.data.map { it[lastViewedKey] ?: BuildConfig.VERSION_CODE }

    suspend fun setLastViewed(versionCode: Int) {
        withContext(Dispatchers.IO) {
            store.edit { it[lastViewedKey] = versionCode }
        }
    }
}