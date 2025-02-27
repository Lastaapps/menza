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

package cz.lastaapps.menza.features.settings.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.ui.vm.AppThemeViewModel
import cz.lastaapps.menza.features.settings.ui.widget.apptheme.AppThemeItem
import cz.lastaapps.menza.features.settings.ui.widget.apptheme.DarkThemeChooser
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun AppThemeScreen(
    onComplete: () -> Unit,
    viewModel: AppThemeViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.flowState

    AppThemeEffects(viewModel)

    AppThemeContent(
        themes = state.availableThemes,
        selectedTheme = state.theme ?: return,
        selectedDarkMode = state.darkMode ?: return,
        onAppTheme = viewModel::setAppTheme,
        onDarkMode = viewModel::setDarkMode,
        onComplete = onComplete,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun AppThemeEffects(viewModel: AppThemeViewModel) {
}

@Composable
private fun AppThemeContent(
    themes: ImmutableList<AppThemeType>,
    selectedTheme: AppThemeType,
    selectedDarkMode: DarkMode,
    onAppTheme: (AppThemeType) -> Unit,
    onDarkMode: (DarkMode) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.spacedBy(
                Padding.MidLarge,
                Alignment.CenterVertically,
            ),
    ) {
        item {
            Text(
                text = stringResource(R.string.settings_theme_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
            )
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.settings_theme_dark_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                DarkThemeChooser(
                    selectedDarkMode,
                    onDarkMode,
                )
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stringResource(R.string.settings_theme_app_title),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                )
                themes.forEach { theme ->
                    AppThemeItem(
                        theme = theme,
                        darkMode = selectedDarkMode,
                        isSelected = selectedTheme == theme,
                        onClick = { onAppTheme(theme) },
                    )
                }
            }
        }

        item {
            Button(onClick = onComplete) {
                Text(stringResource(R.string.button_continue))
            }
        }

        item {
            Spacer(modifier = Modifier.height(42.dp))
        }
    }
}
