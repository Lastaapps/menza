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

package cz.lastaapps.core.ui.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

interface Appearing {
    fun onAppeared()
}

@Composable
fun HandleAppear(appearing: Appearing) {
    val key = remember(appearing) {
        buildString {
            append(Appearing::class.qualifiedName)
            append('_')
            append(appearing::class.simpleName)
            append('_')
            append(appearing.hashCode().toString())
        }
    }

    var shown by rememberSaveable(
        appearing.hashCode(),
        key = key,
    ) { mutableStateOf(false) }

    LaunchedEffect(appearing) {
        if (!shown) {
            shown = true
            appearing.onAppeared()
        }
    }
}
