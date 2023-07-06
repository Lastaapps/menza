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

package cz.lastaapps.menza.ui.locals

import android.app.Activity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.lighthousegames.logging.logging

val LocalWindowSize = compositionLocalOf { WindowSizeClass.calculateFromSize(DpSize.Zero) }
val LocalWindowWidth = compositionLocalOf { WindowWidthSizeClass.Compact }
val LocalWindowHeight = compositionLocalOf { WindowHeightSizeClass.Compact }
val LocalMayBeFlipCover = compositionLocalOf { false }

@Composable
fun WithLocalWindowSizes(
    activity: Activity,
    content: @Composable () -> Unit,
) {
    val size = calculateWindowSizeClass(activity)
    BoxWithConstraints {
        val mayBeFlipCover =
            (maxWidth == 512.dp && maxHeight == 260.dp) // same for Flip 3 and 4
                    || (maxWidth == 512.dp && maxHeight == 245.dp) // when bottom bar is enabled

        LaunchedEffect(size, mayBeFlipCover) {
            logging("WindowSize").let { log ->
                log.debug { "New window width class:  ${size.widthSizeClass}" }
                log.debug { "New window height class: ${size.heightSizeClass}" }
                log.debug { "New may be flip cover:   $mayBeFlipCover" }
            }
        }

        CompositionLocalProvider(
            LocalWindowSize provides size,
            LocalWindowWidth provides size.widthSizeClass,
            LocalWindowHeight provides size.heightSizeClass,
            LocalMayBeFlipCover provides mayBeFlipCover,
        ) { content() }
    }
}
