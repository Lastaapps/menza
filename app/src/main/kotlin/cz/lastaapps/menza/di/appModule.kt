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
import cz.lastaapps.menza.features.main.di.mainModule
import cz.lastaapps.menza.features.root.domain.usecase.AppSetupFinishedUC
import cz.lastaapps.menza.features.root.domain.usecase.IsAppSetUpUC
import cz.lastaapps.menza.features.root.ui.RootViewModel
import cz.lastaapps.menza.features.settings.di.settingsModule
import cz.lastaapps.menza.features.starting.di.startingModule
import cz.lastaapps.menza.features.today.di.todayModule
import cz.lastaapps.menza.ui.dests.others.whatsnew.WhatsNewDataStore
import cz.lastaapps.menza.ui.layout.menza.MenzaOrderDataStore
import cz.lastaapps.scraping.scrapingModule
import cz.lastaapps.storage.db.storageDbModule
import cz.lastaapps.storage.repo.repoModule
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val appModule = module {
    includes(
        legacyModule,
        apiModule,
        coreModule,
        crashModule,
        scrapingModule,
        repoModule,
        storageDbModule,
        settingsModule,
        mainModule,
        startingModule,
        todayModule,
    )

    single<MenzaOrderDataStore> { MenzaOrderDataStore(get()) }
    single<WhatsNewDataStore> { WhatsNewDataStore(get()) }

    factoryOf(::IsAppSetUpUC)
    factoryOf(::AppSetupFinishedUC)

    viewModelOf(::RootViewModel)
}
