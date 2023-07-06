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

package cz.lastaapps.menza.ui.root

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import cz.lastaapps.menza.ui.locals.LocalMenuBackArrow

class MenuBackArrow {
    private val actions = mutableStateListOf<() -> Unit>()

    @Composable
    fun shouldShowBackArrow(): Boolean = actions.isNotEmpty()

    fun registerBackEvent(event: () -> Unit) = actions.add(event)
    fun unregisterBackEvent(event: () -> Unit) = actions.remove(event)

    fun runLast() {
        actions.lastOrNull()?.invoke()
    }
}

@Composable
fun BackArrow(
    enabled: Boolean = true, action: () -> Unit
) {
    if (enabled) {
        val menuBackArrow = LocalMenuBackArrow.current
        DisposableEffect(key1 = action) {
            menuBackArrow.registerBackEvent(action)
            onDispose {
                menuBackArrow.unregisterBackEvent(action)
            }
        }
    }
    BackHandler(enabled, action)
}