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

import cz.lastaapps.api.main.di.apiModule
import cz.lastaapps.core.di.coreModule
import cz.lastaapps.crash.crashModule
import cz.lastaapps.menza.init.InitViewModel
import cz.lastaapps.menza.ui.dests.info.InfoViewModel
import cz.lastaapps.menza.ui.dests.others.crashes.CrashesViewModel
import cz.lastaapps.menza.ui.dests.others.privacy.PrivacyStore
import cz.lastaapps.menza.ui.dests.others.privacy.PrivacyViewModel
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewDataStore
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewViewModel
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.SettingsStore
import cz.lastaapps.menza.ui.dests.today.AllergenViewModel
import cz.lastaapps.menza.ui.dests.today.TodayViewModel
import cz.lastaapps.menza.ui.dests.week.WeekViewModel
import cz.lastaapps.menza.ui.layout.menza.MenzaOrderDataStore
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.RootViewModel
import cz.lastaapps.scraping.scrapingModule
import cz.lastaapps.storage.db.storageDbModule
import cz.lastaapps.storage.repo.repoModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    includes(
        apiModule,
        coreModule,
        crashModule,
        scrapingModule,
        repoModule,
        storageDbModule,
    )

    single<SettingsStore> { SettingsStore(get(), CoroutineScope(Dispatchers.IO)) }
    single<PrivacyStore> { PrivacyStore(get()) }
    single<MenzaOrderDataStore> { MenzaOrderDataStore(get()) }
    single<WhatsNewDataStore> { WhatsNewDataStore(get()) }


    factory<TodayRepoFactory> { TodayRepoFactoryImpl(get()) }
    factory<WeekRepoFactory> { WeekRepoFactoryImpl(get()) }

    viewModel<InitViewModel> { InitViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel<InfoViewModel> { InfoViewModel(get(), get(), get(), get()) }
    viewModel<SettingsViewModel> {
        SettingsViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    viewModel<MenzaViewModel> { MenzaViewModel(get(), get(), get(), get()) }
    viewModel<PrivacyViewModel> { PrivacyViewModel(get()) }
    viewModel<WhatsNewViewModel> { WhatsNewViewModel(get(), get()) }
    viewModel<CrashesViewModel> { CrashesViewModel(get()) }
    viewModel<AllergenViewModel> { AllergenViewModel(get()) }
    viewModel<TodayViewModel> { TodayViewModel(get(), get()) }
    viewModel<WeekViewModel> { WeekViewModel(get()) }
    viewModel<RootViewModel> { RootViewModel(get(), get()) }
}