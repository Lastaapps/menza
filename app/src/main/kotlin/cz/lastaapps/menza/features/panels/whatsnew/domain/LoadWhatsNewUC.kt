/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.panels.whatsnew.domain

import cz.lastaapps.core.data.AssetsProvider
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.other.domain.model.WhatsNewInfo
import java.util.Locale

internal class LoadWhatsNewUC(
    context: UCContext,
    private val assets: AssetsProvider,
) : UseCase(context) {

    private val log = localLogger()

    companion object {
        private const val sourceDir = "changelogs"
    }

    suspend operator fun invoke(): Map<Locale, Set<WhatsNewInfo>> = launch {
        val map = HashMap<Locale, MutableSet<WhatsNewInfo>>()

        val languages = assets.listDirectory(sourceDir)

        languages.asSequence()
            .forEach { localeDir ->
                log.i { "Loading what's new for $localeDir" }
                val path = "$sourceDir/$localeDir"
                val files = assets.listDirectory(path)
                val language = Locale.forLanguageTag(localeDir)

                files.forEach { file ->
                    val versionCode = file.removeSuffix(".txt").toLong()
                    val changes = assets.readFile("$path/$file")
                    map.getOrPut(language) { mutableSetOf() }
                        .add(WhatsNewInfo(versionCode, changes))
                }
            }
        map
    }
}
