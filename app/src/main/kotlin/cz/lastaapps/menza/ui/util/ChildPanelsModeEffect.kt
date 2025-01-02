/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

@file:OptIn(ExperimentalDecomposeApi::class)

package cz.lastaapps.menza.ui.util

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import cz.lastaapps.menza.ui.locals.FoldingClass
import cz.lastaapps.menza.ui.locals.LocalFoldProvider
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.locals.isTableTopPosture

@Composable
fun ChildPanelsModeEffect(
    onModeChange: (ChildPanelsMode) -> Unit,
    widthClass: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    val mode =
        if (widthClass <
            WindowWidthSizeClass.Expanded
        ) {
            ChildPanelsMode.SINGLE
        } else {
            ChildPanelsMode.DUAL
        }

    DisposableEffect(onModeChange, mode) {
        onModeChange(mode)
        onDispose {}
    }
}

/**
 * Adds support for table top folding feature.
 */
@Composable
fun ChildPanelsModeFoldingEffect(
    onModeChange: (ChildPanelsMode) -> Unit,
    widthClass: WindowWidthSizeClass = LocalWindowWidth.current,
    foldingFeature: FoldingClass = LocalFoldProvider.current,
) {
    val mode =
        (foldingFeature as? FoldingClass.Supported)
            ?.takeIf { it.foldingFeature.isTableTopPosture() }
            ?.let { ChildPanelsMode.DUAL }
            ?: when {
                widthClass < WindowWidthSizeClass.Expanded -> ChildPanelsMode.SINGLE
                else -> ChildPanelsMode.DUAL
            }

    DisposableEffect(onModeChange, mode) {
        onModeChange(mode)
        onDispose {}
    }
}
