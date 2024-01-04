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

package cz.lastaapps.menza.ui

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import cz.lastaapps.menza.features.root.ui.RootViewModel
import cz.lastaapps.menza.features.settings.domain.model.shouldUseDark
import cz.lastaapps.menza.ui.theme.AppTheme

@Composable
internal fun ApplyAppTheme(
    viewModel: RootViewModel,
    activity: ComponentActivity,
    content: @Composable () -> Unit,
) {
    val state by viewModel.flowState
    if (state.isReady) {
        AppTheme(
            theme = state.appTheme,
            darkMode = state.darkMode,
            colorSystemBars = true,
            content = content,
        )

        val darkTheme = state.darkMode.shouldUseDark()
        DisposableEffect(darkTheme) {
            // taken from default enableEdgeToEdgeImpl
            val lightScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
            val darkScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                ) { darkTheme },
                navigationBarStyle = SystemBarStyle.auto(
                    lightScrim,
                    darkScrim,
                ) { darkTheme },
            )
            onDispose { }
        }
    }
}
