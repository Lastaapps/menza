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

package cz.lastaapps.menza.features.settings.data

import android.content.Context
import android.os.Build
import cz.lastaapps.api.core.domain.model.DataLanguage

internal interface DefaultsProvider {
    fun defaultDishLanguage(): DataLanguage
}

internal class DefaultsProviderImpl(
    private val context: Context,
) : DefaultsProvider {
    override fun defaultDishLanguage(): DataLanguage {
        val locates =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales.let { list ->
                    List(list.size()) { list[it] }
                }
            } else {
                @Suppress("DEPRECATION")
                listOf(context.resources.configuration.locale)
            }.map { it.language }

        val slavic = listOf("cs", "sk", "uk", "ru", "pl", "sl", "hr")

        return if (locates.any { it in slavic }) {
            DataLanguage.Czech
        } else {
            DataLanguage.English
        }
    }
}
