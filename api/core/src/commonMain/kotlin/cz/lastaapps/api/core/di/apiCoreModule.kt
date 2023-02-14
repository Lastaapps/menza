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

package cz.lastaapps.api.core.di

import cz.lastaapps.api.core.data.SyncProcessorImpl
import cz.lastaapps.api.core.data.ValidityCheckerImpl
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Subsystem
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FEL
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FS
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module
import org.lighthousegames.logging.logging

internal expect val platform: Module

val apiCoreModule = module {
    includes(platform)

    // Once global
    singleOf(::MenzaScopeStore)

    val scopeStoreMutex = Mutex()
    val scopeLog = logging("MenzaKoinScope")
    factory { (menza: MenzaType) ->
        runBlocking(Dispatchers.Default) {
            scopeStoreMutex.withLock {
                val store = get<MenzaScopeStore>()
                store.getOrPut(menza) {
                    scopeLog.i { "Creating scope for $menza" }

                    when (menza) {
                        Strahov -> createScope<Strahov>()
                        is Subsystem -> createScope<Subsystem>()
                        FEL -> createScope<FEL>()
                        FS -> createScope<FS>()
                    }
                }
            }
        }
    }

    factory { (menza: MenzaType) ->
        get<MenzaTypeScope> { parametersOf(menza) }
            .scope.get<TodayDishRepo> { parametersOf(menza) }
    }
    factory { (menza: MenzaType) ->
        get<MenzaTypeScope> { parametersOf(menza) }
            .scope.get<InfoRepo> { parametersOf(menza) }
    }
    factory { (menza: MenzaType) ->
        get<MenzaTypeScope> { parametersOf(menza) }
            .scope.get<WeekDishRepo> { parametersOf(menza) }
    }

    singleOf(::ValidityCheckerImpl) bind ValidityChecker::class
    factoryOf(::SyncProcessorImpl) bind SyncProcessor::class
}

@JvmInline
private value class MenzaTypeScope(val scope: Scope)

private inline fun <reified T : MenzaType> Scope.createScope() =
    MenzaTypeScope(getKoin().createScope<T>())

private class MenzaScopeStore : MutableMap<MenzaType, MenzaTypeScope> by HashMap()
