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

package cz.lastaapps.menza.api.agata.di

import cz.lastaapps.api.core.di.registerMenzaType
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.api.CafeteriaApiImpl
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.api.DishApiImpl
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.api.SubsystemApiImpl
import cz.lastaapps.menza.api.agata.data.AgataDatabaseFactory
import cz.lastaapps.menza.api.agata.data.HashStoreImpl
import cz.lastaapps.menza.api.agata.data.createAgataClient
import cz.lastaapps.menza.api.agata.data.repo.InfoRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.InfoStrahovRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.MenzaStrahovRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.MenzaSubsystemRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.TodayDishStrahovRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.TodayDishSubsystemRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.WeekDishRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.WeekRepoStrahovImpl
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.AgataBEConfig
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platform: Module

val apiAgataModule = module {
    includes(platform)

    factoryOf(::CafeteriaApiImpl) bind CafeteriaApi::class
    factoryOf(::DishApiImpl) bind DishApi::class
    factoryOf(::SubsystemApiImpl) bind SubsystemApi::class

    singleOf(::HashStoreImpl) bind HashStore::class
    single { AgataDatabaseFactory.createDatabase(get()) }
    factory { AgataBEConfig.prod }
    single { createAgataClient(get(), get()) }

    // Repos
    // Menza list
    singleOf(::MenzaSubsystemRepoImpl) bind MenzaRepo::class

    registerMenzaType<MenzaType.Agata.Strahov>(
        menzaRepo = { MenzaSubsystemRepoImpl(get(), get(), get(), get(), get()) },
        dishRepo = { TodayDishStrahovRepoImpl(get(), get(), get(), get(), get(), get()) },
        infoRepo = { InfoStrahovRepoImpl },
        weekRepo = { WeekRepoStrahovImpl },
    )

    registerMenzaType<MenzaType.Agata.Subsystem>(
        menzaRepo = { MenzaStrahovRepoImpl },
        dishRepo = { menza ->
            TodayDishSubsystemRepoImpl(
                menza.subsystemId,
                get(), get(), get(), get(), get(), get(), get(),
            )
        },
        infoRepo = { menza ->
            InfoRepoImpl(menza.subsystemId, get(), get(), get(), get(), get())
        },
        weekRepo = { menza ->
            WeekDishRepoImpl(menza.subsystemId, get(), get(), get(), get())
        },
    )
}
