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

package cz.lastaapps.menza.features.settings.domain.model

import androidx.compose.runtime.Immutable
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.MenzaType

@Immutable
internal data class AppSettings(
    val initialMenzaMode: InitialSelectionBehaviour,
    val latestMenza: MenzaType?,
    val preferredMenza: MenzaType?,
    val isAppSetupFinished: Boolean,
    val isSettingsEverOpened: Boolean,
    val priceType: PriceType,
    val darkMode: DarkMode,
    val appTheme: AppThemeType?,
    val imageScale: Float,
    val imagesOnMetered: Boolean,
    val dataLanguage: DataLanguage,
    val todayViewMode: DishListMode,
    val useOliverRows: Boolean,
    val balanceWarningThreshold: Int,
    val alternativeNavigation: Boolean,
    val isDishListModeChosen: Boolean,
) {
    companion object {
        // Used for previews
        val default =
            AppSettings(
                initialMenzaMode = InitialSelectionBehaviour.Specific,
                latestMenza = MenzaType.Testing.Kocourkov,
                preferredMenza = MenzaType.Testing.Kocourkov,
                isAppSetupFinished = true,
                isSettingsEverOpened = true,
                priceType = PriceType.Normal,
                darkMode = DarkMode.System,
                appTheme = AppThemeType.Agata,
                imageScale = 1f,
                imagesOnMetered = true,
                dataLanguage = DataLanguage.Czech,
                todayViewMode = DishListMode.COMPACT,
                useOliverRows = true,
                balanceWarningThreshold = 256,
                alternativeNavigation = true,
                isDishListModeChosen = false,
            )
    }
}
