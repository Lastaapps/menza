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

package cz.lastaapps.menza.features.other.util

import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.annotation.RequiresApi
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf

@Suppress("DEPRECATION")
fun Context.getLocales(): ImmutableList<Locale> {
    val config = resources.configuration
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        config.locales.toList()
    } else persistentListOf(config.locale)
}

@RequiresApi(Build.VERSION_CODES.N)
private fun LocaleList.toList(): ImmutableList<Locale> = persistentListOf<Locale>().mutate { list ->
    for (i in 0 until size())
        list += get(i)
}
