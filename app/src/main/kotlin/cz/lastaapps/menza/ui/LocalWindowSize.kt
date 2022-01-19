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

package cz.lastaapps.menza.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.window.layout.WindowMetricsCalculator
import org.lighthousegames.logging.logging

sealed class WindowSizeClass private constructor(val name: String) {
    object COMPACT : WindowSizeClass("Compact")
    object MEDIUM : WindowSizeClass("Medium")
    object EXPANDED : WindowSizeClass("Expanded")
}

val log = logging(WindowSizeClass::class.simpleName)

val LocalWindowWidth = compositionLocalOf<WindowSizeClass> { WindowSizeClass.COMPACT }
val LocalWindowHeight = compositionLocalOf<WindowSizeClass> { WindowSizeClass.COMPACT }

@Composable
fun WithLocalWindowSizes(
    activity: Activity,
    content: @Composable () -> Unit
) {
    val size = activity.rememberWindowDpSize()
    CompositionLocalProvider(LocalWindowWidth provides getWindowWidthClass(size)) {
        CompositionLocalProvider(LocalWindowHeight provides getWindowHeightClass(size)) {
            content()
        }
    }
}

//Stolen from https://github.com/android/compose-samples/blob/d38047520c00d5eed71eb731b1fa5ecd99f59a32/JetNews/app/src/main/java/com/example/jetnews/utils/WindowSize.kt
@Composable
fun Activity.rememberWindowDpSize(): DpSize {
    // Get the size (in pixels) of the window
    val windowSize = rememberWindowSize()

    // Convert the window size to [Dp]
    return with(LocalDensity.current) {
        windowSize.toDpSize()
    }
}

/**
 * Remembers the [Size] in pixels of the window corresponding to the current window metrics.
 */
@Composable
private fun Activity.rememberWindowSize(): Size {
    val configuration = LocalConfiguration.current
    // WindowMetricsCalculator implicitly depends on the configuration through the activity,
    // so re-calculate it upon changes.
    val windowMetrics = remember(configuration) {
        WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(this)
    }
    return windowMetrics.bounds.toComposeRect().size
}

private fun getWindowWidthClass(windowDpSize: DpSize): WindowSizeClass = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 600.dp -> WindowSizeClass.COMPACT
    windowDpSize.width < 840.dp -> WindowSizeClass.MEDIUM
    else -> WindowSizeClass.EXPANDED
}.also {
    log.i { "Layout width mode: ${it.name}" }
}

private fun getWindowHeightClass(windowDpSize: DpSize): WindowSizeClass = when {
    windowDpSize.width < 0.dp -> throw IllegalArgumentException("Dp value cannot be negative")
    windowDpSize.width < 480.dp -> WindowSizeClass.COMPACT
    windowDpSize.width < 900.dp -> WindowSizeClass.MEDIUM
    else -> WindowSizeClass.EXPANDED
}.also {
    log.i { "Layout height mode: ${it.name}" }
}
