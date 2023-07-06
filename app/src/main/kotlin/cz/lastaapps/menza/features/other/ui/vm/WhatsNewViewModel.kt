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

package cz.lastaapps.menza.features.other.ui.vm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.menza.features.other.data.WhatsNewDataStore
import cz.lastaapps.menza.features.other.domain.model.WhatsNewInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.lighthousegames.logging.logging

class WhatsNewViewModel(
    private val app: Application,
    private val store: WhatsNewDataStore,
) : ViewModel() {

    companion object {
        private val log = logging()
        private const val sourceDir = "changelogs"
    }

    private val data = MutableStateFlow<Map<Locale, MutableSet<WhatsNewInfo>>>(emptyMap())

    init {
        viewModelScope.launch {
            data.emit(loadAssets())
        }
    }

    private suspend fun loadAssets(): Map<Locale, MutableSet<WhatsNewInfo>> =
        withContext(Dispatchers.IO) {
            val map = HashMap<Locale, MutableSet<WhatsNewInfo>>()
            val assets = app.assets

            @Suppress("BlockingMethodInNonBlockingContext")
            val languages = assets.list(sourceDir) ?: Array(0) { "" }

            languages.asSequence()
                .forEach { localeDir ->
                    log.i { "Loading what's new for $localeDir" }
                    val path = "$sourceDir/$localeDir"
                    val files = assets.list(path)
                    val language = Locale.forLanguageTag(localeDir)

                    files?.forEach { file ->
                        val stream = assets.open("$path/$file")
                        val versionCode = file.removeSuffix(".txt").toLong()
                        val content = BufferedReader(InputStreamReader(stream)).readText()
                        map.getOrPut(language) { mutableSetOf() }
                            .add(WhatsNewInfo(versionCode, content))
                    }
                }
            return@withContext map
        }

    fun getDataForLocales(locales: ImmutableList<Locale>): Flow<List<WhatsNewInfo>> =
        data.map { map ->
            val locale = locales.firstOrNull { map.containsKey(it) } ?: Locale.US
            map.getOrDefault(locale, emptySet())
        }.map { set -> set.sorted() }

    fun shouldShow(versionCode: Int): Flow<Boolean> =
        store.lastViewed.map { it < versionCode }

    fun dismissed(versionCode: Int) {
        viewModelScope.launch {
            store.setLastViewed(versionCode)
        }
    }
}