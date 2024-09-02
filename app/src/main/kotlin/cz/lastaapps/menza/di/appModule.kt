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

package cz.lastaapps.menza.di

import cz.lastaapps.api.main.di.apiModule
import cz.lastaapps.api.main.domain.usecase.GetRequestParamsUC
import cz.lastaapps.core.data.AppInfoProvider
import cz.lastaapps.core.di.coreModule
import cz.lastaapps.crash.crashModule
import cz.lastaapps.menza.data.AndroidAppInfoProvider
import cz.lastaapps.menza.domain.usecase.GetRequestParamsUCImpl
import cz.lastaapps.menza.features.info.di.infoModule
import cz.lastaapps.menza.features.main.di.mainModule
import cz.lastaapps.menza.features.other.di.otherModule
import cz.lastaapps.menza.features.panels.di.panelsModule
import cz.lastaapps.menza.features.root.di.rootModule
import cz.lastaapps.menza.features.settings.di.settingsModule
import cz.lastaapps.menza.features.starting.di.startingModule
import cz.lastaapps.menza.features.today.di.todayModule
import cz.lastaapps.menza.features.week.di.weekModule
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule =
    module {
        includes(
            apiModule,
            coreModule,
            crashModule,
            rootModule,
            settingsModule,
            mainModule,
            startingModule,
            todayModule,
            weekModule,
            otherModule,
            infoModule,
            panelsModule,
        )

        factoryOf(::AndroidAppInfoProvider) bind AppInfoProvider::class
        singleOf(::GetRequestParamsUCImpl) bind GetRequestParamsUC::class
    }
