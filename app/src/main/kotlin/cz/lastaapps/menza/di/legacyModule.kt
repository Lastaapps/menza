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

package cz.lastaapps.menza.di

import cz.lastaapps.menza.ui.dests.info.InfoViewModel
import cz.lastaapps.menza.ui.dests.others.crashes.CrashesViewModel
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewViewModel
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.today.AllergenViewModel
import cz.lastaapps.menza.ui.dests.today.TodayViewModel
import cz.lastaapps.menza.ui.dests.week.WeekViewModel
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.RootViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val legacyModule = module {

    factory<TodayRepoFactory> { TodayRepoFactoryImpl(get()) }
    factory<WeekRepoFactory> { WeekRepoFactoryImpl(get()) }

    viewModel<InfoViewModel> { InfoViewModel(get(), get(), get(), get()) }
    viewModel<SettingsViewModel> {
        SettingsViewModel(get(), get(), get(), get(), get(), get(), get())
    }
    viewModel<MenzaViewModel> { MenzaViewModel(get(), get(), get()) }
    viewModel<WhatsNewViewModel> { WhatsNewViewModel(get(), get()) }
    viewModel<CrashesViewModel> { CrashesViewModel(get()) }
    viewModel<AllergenViewModel> { AllergenViewModel(get()) }
    viewModel<TodayViewModel> { TodayViewModel(get(), get()) }
    viewModel<WeekViewModel> { WeekViewModel(get()) }
    viewModel<RootViewModel> { RootViewModel(get()) }
}