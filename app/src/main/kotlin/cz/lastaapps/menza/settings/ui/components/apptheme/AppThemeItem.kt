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

package cz.lastaapps.menza.settings.ui.components.apptheme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.settings.domain.model.AppThemeType
import cz.lastaapps.menza.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.settings.domain.model.AppThemeType.CTU
import cz.lastaapps.menza.settings.domain.model.AppThemeType.System
import cz.lastaapps.menza.settings.domain.model.AppThemeType.Uwu
import cz.lastaapps.menza.settings.domain.model.DarkMode
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.MenzaPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppThemeItem(
    theme: AppThemeType,
    darkMode: DarkMode,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppTheme(
        darkMode = darkMode,
        theme = theme,
    ) {
        val scale by animateFloatAsState(
            if (isSelected) 1f else AppThemeItem.unselectedScale
        )
        Surface(
            shape = MaterialTheme.shapes.large,
            modifier = modifier.scale(scale),
        ) {
            Card(
                onClick = onClick,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.padding(MenzaPadding.Small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MenzaPadding.MidLarge),
                    modifier = Modifier
                        .padding(MenzaPadding.Medium)
                        .fillMaxWidth(),
                ) {
                    ColorPreview()
                    Text(theme.name())
                }
            }
        }
    }
}

@Composable
private fun AppThemeType.name() =
    stringResource(
        when (this) {
            System -> R.string.settings_theme_app_system
            Agata -> R.string.settings_theme_app_agata
            CTU -> R.string.settings_theme_app_ctu
            Uwu -> R.string.settings_theme_app_uwu
        }
    )

@Composable
private fun ColorPreview(modifier: Modifier = Modifier) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderColor = MaterialTheme.colorScheme.inversePrimary

    val strokeWidth = AppThemeItem.borderStrokeSize

    Canvas(modifier = modifier.size(AppThemeItem.colorPreviewSize)) {
        drawCircle(primaryColor)
        drawCircle(borderColor, style = Stroke(strokeWidth))
    }
}

private object AppThemeItem {
    const val unselectedScale = .97f
    val colorPreviewSize = 60.dp
    val borderStrokeSize: Float
        @Composable
        get() = with(LocalDensity.current) { 2.dp.toPx() }
}

@Preview
@Composable
private fun ThemeItemPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(MenzaPadding.Medium)) {
        var selected by remember { mutableStateOf(1) }
        AppThemeType.values().forEachIndexed { index, theme ->
            AppThemeItem(theme, DarkMode.Light, selected != index, { selected = index })
        }
    }
}
