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

import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.api.CafeteriaApiImpl
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.api.DishApiImpl
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.api.SubsystemApiImpl
import cz.lastaapps.menza.api.agata.data.AgataDatabaseFactory
import cz.lastaapps.menza.api.agata.data.HashStoreImpl
import cz.lastaapps.menza.api.agata.data.SyncProcessorImpl
import cz.lastaapps.menza.api.agata.data.repo.DishListRepoStrahovImpl
import cz.lastaapps.menza.api.agata.data.repo.DishListRepoSubsystemImpl
import cz.lastaapps.menza.api.agata.data.repo.InfoRepositoryImpl
import cz.lastaapps.menza.api.agata.data.repo.InfoRepositoryStrahovImpl
import cz.lastaapps.menza.api.agata.data.repo.MenzaListRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.WeekDishRepoImpl
import cz.lastaapps.menza.api.agata.data.repo.WeekDishRepoStrahovImpl
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.MenzaType.Strahov
import cz.lastaapps.menza.api.agata.domain.model.MenzaType.Subsystem
import cz.lastaapps.menza.api.agata.domain.model.common.Menza
import cz.lastaapps.menza.api.agata.domain.repo.DishListRepo
import cz.lastaapps.menza.api.agata.domain.repo.InfoRepository
import cz.lastaapps.menza.api.agata.domain.repo.MenzaListRepo
import cz.lastaapps.menza.api.agata.domain.repo.WeekRepository
import cz.lastaapps.menza.api.agata.domain.sync.SyncProcessor
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platform: Module

val api_agata_module = module {
    includes(platform)

    factoryOf(::CafeteriaApiImpl) bind CafeteriaApi::class
    factoryOf(::DishApiImpl) bind DishApi::class
    factoryOf(::SubsystemApiImpl) bind SubsystemApi::class
    factoryOf(::SyncProcessorImpl) bind SyncProcessor::class

    singleOf(::HashStoreImpl) bind HashStore::class
    single { AgataDatabaseFactory.createDatabase(get()) }

    // Repos
    // Menza list
    singleOf(::MenzaListRepoImpl) bind MenzaListRepo::class

    // Dish
    single { (menza: Menza) ->
        when (val type = menza.type) {
            is Subsystem -> DishListRepoSubsystemImpl(type.subsystemId, get(), get(), get(), get())
            Strahov -> DishListRepoStrahovImpl(get(), get())
        }
    } bind DishListRepo::class

    // Info
    single { (menza: Menza) ->
        when (val type = menza.type) {
            is Subsystem -> InfoRepositoryImpl(type.subsystemId, get(), get(), get())
            Strahov -> InfoRepositoryStrahovImpl
        }
    } bind InfoRepository::class

    // Week
    single { (menza: Menza) ->
        when (val type = menza.type) {
            is Subsystem -> WeekDishRepoImpl(type.subsystemId, get(), get())
            Strahov -> WeekDishRepoStrahovImpl
        }
    } bind WeekRepository::class
}
