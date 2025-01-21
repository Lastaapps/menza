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

package cz.lastaapps.menza.features.settings.domain

import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.menza.features.settings.domain.model.AppSettings
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import kotlinx.coroutines.flow.Flow

internal interface MainSettingsRepo {
    fun getAllSettings(): Flow<AppSettings>

    suspend fun storeInitialMenzaMode(mode: InitialSelectionBehaviour)

    fun getInitialMenzaMode(): Flow<InitialSelectionBehaviour>

    suspend fun storeLatestMenza(type: MenzaType)

    fun getLatestMenza(): Flow<MenzaType?>

    suspend fun storePreferredMenza(type: MenzaType)

    fun getPreferredMenza(): Flow<MenzaType?>

    suspend fun storeAppSetupFinished()

    fun isAppSetupFinished(): Flow<Boolean>

    suspend fun storeSettingsEverOpened()

    fun isSettingsEverOpened(): Flow<Boolean>

    suspend fun setPriceType(type: PriceType)

    fun getPriceType(): Flow<PriceType>

    suspend fun setDarkMode(mode: DarkMode)

    fun getDarkMode(): Flow<DarkMode>

    suspend fun setAppTheme(theme: AppThemeType)

    fun getAppTheme(): Flow<AppThemeType?>

    suspend fun setImageScale(scale: Float)

    fun getImageScale(): Flow<Float>

    suspend fun setImagesOnMetered(enabled: Boolean)

    fun getImagesOnMetered(): Flow<Boolean>

    suspend fun setDishLanguage(language: DataLanguage)

    fun getDishLanguage(): Flow<DataLanguage>

    suspend fun setCompactTodayView(mode: DishListMode)

    fun isCompactTodayView(): Flow<DishListMode>

    suspend fun setOliverRows(useOliverRows: Boolean)

    fun isOliverRow(): Flow<Boolean>

    suspend fun setBalanceWarningThreshold(threshold: Int)

    fun getBalanceWarningThreshold(): Flow<Int>

    suspend fun setAlternativeNavigation(enabled: Boolean)

    fun getAlternativeNavigation(): Flow<Boolean>

    suspend fun dismissDishListModeChosen()

    fun isDishListModeChosen(): Flow<Boolean>
}
