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

@file:OptIn(ExperimentalMaterial3WindowSizeClassApi::class)

package cz.lastaapps.menza.ui.root.locals

import android.app.Activity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.DpSize

val LocalWindowSize = compositionLocalOf { WindowSizeClass.calculateFromSize(DpSize.Zero) }
val LocalWindowWidth = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalWindowHeight = compositionLocalOf { WindowHeightSizeClass.Compact }

@Composable
fun WithLocalWindowSizes(
    activity: Activity,
    content: @Composable () -> Unit,
) {
    val size = calculateWindowSizeClass(activity)
    CompositionLocalProvider(
        LocalWindowSize provides size,
        LocalWindowWidth provides size.widthSizeClass,
        LocalWindowHeight provides size.heightSizeClass,
    ) { content() }
}
