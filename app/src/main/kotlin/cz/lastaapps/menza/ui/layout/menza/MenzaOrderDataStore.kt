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

package cz.lastaapps.menza.ui.layout.menza

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MenzaOrderDataStore(context: Context) {
    companion object {
        private val Context.menzaDataStore by preferencesDataStore("menza_order")
    }

    private val store = context.menzaDataStore

    private fun getItemOrder(key: String): Flow<Int> =
        store.data.map { it[intPreferencesKey(key)] ?: 0 }.distinctUntilChanged()

    fun getItemOrder(keys: Collection<String>): Map<String, Flow<Int>> {
        val map = HashMap<String, Flow<Int>>()
        keys.forEach { key ->
            map[key] = getItemOrder(key)
        }
        return map
    }

    fun <T : Any> getItemOrder(items: Collection<T>, toKey: (T) -> String): Map<T, Flow<Int>> {
        val map = HashMap<T, Flow<Int>>()
        items.forEach { key ->
            map[key] = getItemOrder(toKey(key))
        }
        return map
    }

    suspend fun <T> setItemOrder(keyOrders: Map<T, Int>, keys: (T) -> String) {
        withContext(Dispatchers.IO) {
            store.edit { pref ->
                keyOrders.forEach { entry ->
                    pref[intPreferencesKey(keys(entry.key))] = entry.value
                }
            }
        }
    }

    suspend fun setItemOrder(keyOrders: Map<String, Int>) {
        withContext(Dispatchers.IO) {
            store.edit { pref ->
                keyOrders.forEach { entry ->
                    pref[intPreferencesKey(entry.key)] = entry.value
                }
            }
        }
    }
}

fun <T : Any> Map<T, Flow<Int>>.simplify(): Flow<Map<T, Int>> {
    val emptyMapFlow = flow { emit(HashMap<T, Int>()) }
    var latestFlow: Flow<MutableMap<T, Int>> = emptyMapFlow
    forEach { entry ->
        latestFlow = latestFlow.combine(entry.value) { map, order ->
            map[entry.key] = order
            map
        }
    }
    return latestFlow
}
