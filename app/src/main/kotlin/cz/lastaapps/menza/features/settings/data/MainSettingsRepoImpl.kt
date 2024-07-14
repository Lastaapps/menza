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

package cz.lastaapps.menza.features.settings.data

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.menza.features.settings.data.datasource.GeneralDataSource
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSource
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import cz.lastaapps.menza.features.settings.domain.model.AppSettings
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class MainSettingsRepoImpl(
    private val initial: InitMenzaDataSource,
    private val general: GeneralDataSource,
    private val defaults: DefaultsProvider,
) : MainSettingsRepo {

    override fun getAllSettings(): Flow<AppSettings> = combine(
        getInitialMenzaMode().distinctUntilChanged(),
        getLatestMenza().distinctUntilChanged(),
        getPreferredMenza().distinctUntilChanged(),
        isAppSetupFinished().distinctUntilChanged(),
        isSettingsEverOpened().distinctUntilChanged(),
        getPriceType().distinctUntilChanged(),
        getDarkMode().distinctUntilChanged(),
        getAppTheme().distinctUntilChanged(),
        getImageScale().distinctUntilChanged(),
        getImagesOnMetered().distinctUntilChanged(),
        getDishLanguage().distinctUntilChanged(),
        isCompactTodayView().distinctUntilChanged(),
        isOliverRow().distinctUntilChanged(),
        getBalanceWarningThreshold().distinctUntilChanged(),
        getAlternativeNavigation().distinctUntilChanged(),
    ) { arr ->
        AppSettings(
            initialMenzaMode = arr[0] as InitialSelectionBehaviour,
            latestMenza = arr[1] as MenzaType?,
            preferredMenza = arr[2] as MenzaType?,
            isAppSetupFinished = arr[3] as Boolean,
            isSettingsEverOpened = arr[4] as Boolean,
            priceType = arr[5] as PriceType,
            darkMode = arr[6] as DarkMode,
            appTheme = arr[7] as AppThemeType?,
            imageScale = arr[8] as Float,
            imagesOnMetered = arr[9] as Boolean,
            dishLanguage = arr[10] as DishLanguage,
            todayViewMode = arr[11] as DishListMode,
            useOliverRows = arr[12] as Boolean,
            balanceWarningThreshold = arr[13] as Int,
            alternativeNavigation = arr[14] as Boolean,
        )
    }.distinctUntilChanged()

    override suspend fun storeInitialMenzaMode(mode: InitialSelectionBehaviour) =
        initial.storeInitialMenzaMode(mode)

    override fun getInitialMenzaMode(): Flow<InitialSelectionBehaviour> =
        initial.getInitialMenzaMode()


    override suspend fun storeLatestMenza(type: MenzaType) =
        initial.storeLatestMenza(type)

    override fun getLatestMenza(): Flow<MenzaType?> =
        initial.getLatestMenza()

    override suspend fun storePreferredMenza(type: MenzaType) =
        initial.storePreferredMenza(type)

    override fun getPreferredMenza(): Flow<MenzaType?> =
        initial.getPreferredMenza()

    override suspend fun storeAppSetupFinished() =
        general.storeAppSetupFinished()

    override fun isAppSetupFinished(): Flow<Boolean> =
        general.isAppSetupFinished()

    override suspend fun storeSettingsEverOpened() =
        general.storeSettingsEverOpened()

    override fun isSettingsEverOpened(): Flow<Boolean> =
        general.isSettingsEverOpened()

    override suspend fun setPriceType(type: PriceType) =
        general.setPriceType(type)

    override fun getPriceType(): Flow<PriceType> =
        general.getPriceType()

    override suspend fun setDarkMode(mode: DarkMode) =
        general.setDarkMode(mode)

    override fun getDarkMode(): Flow<DarkMode> =
        general.getDarkMode()

    override suspend fun setAppTheme(theme: AppThemeType) =
        general.setAppTheme(theme)

    override fun getAppTheme(): Flow<AppThemeType?> =
        general.getAppTheme()

    override suspend fun setImageScale(scale: Float) =
        general.setImageScale(scale)

    override fun getImageScale(): Flow<Float> =
        general.getImageScale()

    override suspend fun setImagesOnMetered(enabled: Boolean) =
        general.setImagesOnMetered(enabled)

    override fun getImagesOnMetered(): Flow<Boolean> =
        general.getImagesOnMetered()

    override suspend fun setDishLanguage(language: DishLanguage) =
        general.setDishLanguage(language)

    override fun getDishLanguage(): Flow<DishLanguage> =
        general.getDishLanguage().map { it ?: defaults.defaultDishLanguage() }

    override suspend fun setCompactTodayView(mode: DishListMode) =
        general.setCompactTodayView(mode)

    override fun isCompactTodayView(): Flow<DishListMode> =
        general.isCompactTodayView()
            .map { it ?: DishListMode.COMPACT }

    override suspend fun setOliverRows(useOliverRows: Boolean) =
        general.setOliverRow(useOliverRows)

    override fun isOliverRow(): Flow<Boolean> =
        general.isOliverRow()
            .map { it ?: true }

    override suspend fun setBalanceWarningThreshold(threshold: Int) =
        general.setBalanceWarningThreshold(threshold)

    override fun getBalanceWarningThreshold(): Flow<Int> =
        general.getBalanceWarningThreshold()
            .map { it ?: 256 }

    override suspend fun setAlternativeNavigation(enabled: Boolean) =
        general.setAlternativeNavigation(enabled)

    override fun getAlternativeNavigation(): Flow<Boolean> =
        general.getAlternativeNavigation()
            .map { it ?: false }

}
