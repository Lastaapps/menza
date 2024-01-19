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

package cz.lastaapps.menza.features.settings.di

import cz.lastaapps.menza.features.settings.data.DefaultsProvider
import cz.lastaapps.menza.features.settings.data.DefaultsProviderImpl
import cz.lastaapps.menza.features.settings.data.MainSettingsRepoImpl
import cz.lastaapps.menza.features.settings.data.OrderRepoImpl
import cz.lastaapps.menza.features.settings.data.datasource.GeneralDataSource
import cz.lastaapps.menza.features.settings.data.datasource.GeneralDataSourceImpl
import cz.lastaapps.menza.features.settings.data.datasource.GeneralSettings
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSource
import cz.lastaapps.menza.features.settings.data.datasource.InitMenzaDataSourceImpl
import cz.lastaapps.menza.features.settings.data.datasource.InitialSettings
import cz.lastaapps.menza.features.settings.data.datasource.OrderDataSource
import cz.lastaapps.menza.features.settings.data.datasource.OrderDataSourceImpl
import cz.lastaapps.menza.features.settings.data.datasource.OrderSettings
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import cz.lastaapps.menza.features.settings.domain.OrderRepo
import cz.lastaapps.menza.features.settings.domain.usecase.GetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImageScaleRangeUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetSettingsEverOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetShowCzechUC
import cz.lastaapps.menza.features.settings.domain.usecase.OnSettingsOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetShowCzechUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetInitialMenzaModeUI
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetInitialMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetInitialMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetLatestMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedVisibleMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.IsMenzaOrderFromTopUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.SetMenzaOrderFromTopUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.ToggleMenzaVisibilityUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.UpdateMenzaOrderUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetDarkModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetThemeListUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.IsDynamicThemeSupportedUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.SetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.SetDarkModeUC
import cz.lastaapps.menza.features.settings.ui.vm.AppThemeViewModel
import cz.lastaapps.menza.features.settings.ui.vm.ReorderMenzaViewModel
import cz.lastaapps.menza.features.settings.ui.vm.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {

    single { InitialSettings.create(get()) }
    factoryOf(::InitMenzaDataSourceImpl) bind InitMenzaDataSource::class
    single { GeneralSettings.create(get()) }
    factoryOf(::GeneralDataSourceImpl) bind GeneralDataSource::class
    singleOf(::MainSettingsRepoImpl) bind MainSettingsRepo::class
    factoryOf(::DefaultsProviderImpl) bind DefaultsProvider::class

    factoryOf(::AppThemeViewModel)
    factory {
        // @formatter:off
        SettingsViewModel(
            get(), get(), get(), get(), get(),
            get(), get(), get(), get(), get(),
            get(), get(), get(),
        )
        // @formatter:on
    }

    // Theme
    factoryOf(::GetAppThemeUC)
    factoryOf(::GetThemeListUC)
    factoryOf(::SetAppThemeUC)
    factoryOf(::IsDynamicThemeSupportedUC)
    factoryOf(::GetDarkModeUC)
    factoryOf(::SetDarkModeUC)

    // Initial
    factoryOf(::GetInitialMenzaModeUI)
    factoryOf(::GetInitialMenzaUC)
    factoryOf(::SetInitialMenzaUC)
    factoryOf(::SetLatestMenzaUC)
    factoryOf(::SetPreferredMenzaUC)
    factoryOf(::GetPreferredMenzaUC)

    // Others
    factoryOf(::GetImageScaleUC)
    factoryOf(::GetImagesOnMeteredUC)
    factoryOf(::GetPriceTypeUC)
    factoryOf(::GetSettingsEverOpenedUC)
    factoryOf(::GetShowCzechUC)
    factoryOf(::OnSettingsOpenedUC)
    factoryOf(::SetImageScaleUC)
    factoryOf(::GetImageScaleRangeUC)
    factoryOf(::SetImagesOnMeteredUC)
    factoryOf(::SetPriceTypeUC)
    factoryOf(::SetShowCzechUC)
    factoryOf(::GetDishListModeUC)
    factoryOf(::SetDishListModeUC)

    // Menza order
    factoryOf(::ReorderMenzaViewModel)
    singleOf(::OrderRepoImpl) bind OrderRepo::class
    single { OrderSettings.create(get()) }
    factoryOf(::OrderDataSourceImpl) bind OrderDataSource::class
    factoryOf(::GetOrderedMenzaListUC)
    factoryOf(::GetOrderedVisibleMenzaListUC)
    factoryOf(::ToggleMenzaVisibilityUC)
    factoryOf(::UpdateMenzaOrderUC)
    factoryOf(::IsMenzaOrderFromTopUC)
    factoryOf(::SetMenzaOrderFromTopUC)
}
