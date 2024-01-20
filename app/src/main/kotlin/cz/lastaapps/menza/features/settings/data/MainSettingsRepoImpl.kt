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
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MainSettingsRepoImpl(
    private val initial: InitMenzaDataSource,
    private val general: GeneralDataSource,
    private val defaults: DefaultsProvider,
) : MainSettingsRepo {
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

}
