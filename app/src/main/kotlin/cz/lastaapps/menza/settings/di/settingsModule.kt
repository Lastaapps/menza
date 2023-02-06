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

package cz.lastaapps.menza.settings.di

import cz.lastaapps.menza.settings.data.SettingsStore
import cz.lastaapps.menza.settings.domain.usecase.GetDarkModeUC
import cz.lastaapps.menza.settings.domain.usecase.GetImageSizeUC
import cz.lastaapps.menza.settings.domain.usecase.GetImagesOnMeteredUC
import cz.lastaapps.menza.settings.domain.usecase.GetPriceTypeUC
import cz.lastaapps.menza.settings.domain.usecase.GetSettingsEverOpenedUC
import cz.lastaapps.menza.settings.domain.usecase.OnSettingsOpenedUC
import cz.lastaapps.menza.settings.domain.usecase.SetDarkModeUC
import cz.lastaapps.menza.settings.domain.usecase.SetImageSizeUC
import cz.lastaapps.menza.settings.domain.usecase.SetImagesOnMeteredUC
import cz.lastaapps.menza.settings.domain.usecase.SetPriceTypeUC
import cz.lastaapps.menza.settings.domain.usecase.initialmenza.GetInitialMenzaUC
import cz.lastaapps.menza.settings.domain.usecase.initialmenza.SetInitialMenzaUC
import cz.lastaapps.menza.settings.domain.usecase.initialmenza.SetLatestMenzaUC
import cz.lastaapps.menza.settings.domain.usecase.initialmenza.SetPreferredMenzaUC
import cz.lastaapps.menza.settings.domain.usecase.theme.GetAppThemeUC
import cz.lastaapps.menza.settings.domain.usecase.theme.GetThemeListUC
import cz.lastaapps.menza.settings.domain.usecase.theme.IsDynamicThemeSupportedUC
import cz.lastaapps.menza.settings.domain.usecase.theme.SetAppThemeUC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsModule = module {
    single { SettingsStore(get(), CoroutineScope(Dispatchers.IO)) }

    factoryOf(::GetAppThemeUC)
    factoryOf(::GetThemeListUC)
    factoryOf(::SetAppThemeUC)
    factoryOf(::IsDynamicThemeSupportedUC)
    factoryOf(::GetDarkModeUC)
    factoryOf(::GetImageSizeUC)
    factoryOf(::GetImageSizeUC)
    factoryOf(::GetImagesOnMeteredUC)
    factoryOf(::GetInitialMenzaUC)
    factoryOf(::GetPriceTypeUC)
    factoryOf(::GetSettingsEverOpenedUC)
    factoryOf(::OnSettingsOpenedUC)
    factoryOf(::SetDarkModeUC)
    factoryOf(::SetImageSizeUC)
    factoryOf(::SetImagesOnMeteredUC)
    factoryOf(::SetInitialMenzaUC)
    factoryOf(::SetLatestMenzaUC)
    factoryOf(::SetPreferredMenzaUC)
    factoryOf(::SetPriceTypeUC)
}
