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

package cz.lastaapps.api.buffet.di

import cz.lastaapps.api.buffet.api.BuffetApi
import cz.lastaapps.api.buffet.api.BuffetApiImpl
import cz.lastaapps.api.buffet.data.BuffetDatabaseFactory
import cz.lastaapps.api.buffet.data.DishLogicImpl
import cz.lastaapps.api.buffet.data.InfoRepositoryImpl
import cz.lastaapps.api.buffet.data.TodayDishRepository
import cz.lastaapps.api.buffet.data.ValidityStoreImpl
import cz.lastaapps.api.buffet.data.WeekDishRepository
import cz.lastaapps.api.buffet.domain.ValidityStore
import cz.lastaapps.api.buffet.domain.model.toType
import cz.lastaapps.api.core.di.registerMenzaType
import cz.lastaapps.api.core.domain.model.MenzaType
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val platform: Module

val apiBuffetModule = module {
    includes(platform)

    factoryOf(::BuffetApiImpl) bind BuffetApi::class
    singleOf(::ValidityStoreImpl) bind ValidityStore::class

    single { BuffetDatabaseFactory.createDatabase(get()) }

    singleOf(::DishLogicImpl)
    registerMenzaType<MenzaType.Buffet.FS>(
        dishRepo = { TodayDishRepository(it.toType(), get()) },
        infoRepo = { InfoRepositoryImpl(it.toType()) },
        weekRepo = { WeekDishRepository(it.toType(), get()) }
    )
    registerMenzaType<MenzaType.Buffet.FEL>(
        dishRepo = { TodayDishRepository(it.toType(), get()) },
        infoRepo = { InfoRepositoryImpl(it.toType()) },
        weekRepo = { WeekDishRepository(it.toType(), get()) }
    )
}
