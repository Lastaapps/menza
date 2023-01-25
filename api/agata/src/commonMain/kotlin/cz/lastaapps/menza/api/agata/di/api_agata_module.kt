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
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.SyncProcessor
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
}
