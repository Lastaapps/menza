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

package cz.lastaapps.menza.features.settings.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.CTU
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Kitty
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.System
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Uwu
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DarkMode.Dark
import cz.lastaapps.menza.features.settings.domain.model.DarkMode.Light
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour.Ask
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour.Remember
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour.Specific
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset

@Composable
internal fun AppThemeType.name() =
    stringResource(
        when (this) {
            System -> R.string.settings_theme_app_system
            Agata -> R.string.settings_theme_app_agata
            CTU -> R.string.settings_theme_app_ctu
            Uwu -> R.string.settings_theme_app_uwu
            Kitty -> R.string.settings_theme_app_kitty
        },
    )

@Composable
internal fun DarkMode.name() =
    stringResource(
        when (this) {
            Light -> R.string.settings_theme_dark_light
            Dark -> R.string.settings_theme_dark_dark
            DarkMode.System -> R.string.settings_theme_dark_system
        }
    )

@Composable
internal fun PriceType.name() =
    stringResource(
        when (this) {
            Discounted -> R.string.panel_price_discounted
            Normal -> R.string.panel_price_normal
            Unset -> R.string.panel_price_normal
        }
    )

@Composable
internal fun InitialSelectionBehaviour.name() =
    stringResource(
        id = when (this) {
            Ask -> R.string.settings_init_menza_ask
            Remember -> R.string.settings_init_menza_remember
            Specific -> R.string.settings_init_menza_specific
        }
    )
