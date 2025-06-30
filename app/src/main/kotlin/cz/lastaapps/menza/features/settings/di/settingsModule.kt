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
import cz.lastaapps.menza.features.settings.domain.usecase.FullAppReloadUC
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
import cz.lastaapps.menza.features.settings.domain.usecase.settings.DismissDishListModeChooserUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetAppSettingsUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetBalanceWarningThresholdUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetDishLanguageUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetImageScaleRangeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetOliverRowUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetSettingsEverOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.OnSettingsOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetAlternativeNavigationUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetBalanceWarningThresholdUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetCurrencyUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetDishLanguageUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetOliverRow
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetPriceTypeUC
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

val settingsModule =
    module {

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
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
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
        factoryOf(::FullAppReloadUC)

        // Others
        factoryOf(::GetAppSettingsUC)
        factoryOf(::SetAlternativeNavigationUC)
        factoryOf(::GetImageScaleUC)
        factoryOf(::GetImagesOnMeteredUC)
        factoryOf(::GetBalanceWarningThresholdUC)
        factoryOf(::SetBalanceWarningThresholdUC)
        factoryOf(::GetOliverRowUC)
        factoryOf(::GetPriceTypeUC)
        factoryOf(::GetSettingsEverOpenedUC)
        factoryOf(::GetDishLanguageUC)
        factoryOf(::OnSettingsOpenedUC)
        factoryOf(::SetImageScaleUC)
        factoryOf(::GetImageScaleRangeUC)
        factoryOf(::SetImagesOnMeteredUC)
        factoryOf(::SetOliverRow)
        factoryOf(::SetPriceTypeUC)
        factoryOf(::SetDishLanguageUC)
        factoryOf(::GetDishListModeUC)
        factoryOf(::SetDishListModeUC)
        factoryOf(::DismissDishListModeChooserUC)
        factoryOf(::SetCurrencyUC)

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
