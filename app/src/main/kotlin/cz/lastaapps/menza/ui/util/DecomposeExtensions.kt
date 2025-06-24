/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.util

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.getOrCreate
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

// TODO remove once AS supports Kotlin 2.2
private typealias Idk = KoinComponent

context(component: KoinComponent)
inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.getOrCreateKoin(
    qualifier: org.koin.core.qualifier.Qualifier? = null,
    noinline parameters: org.koin.core.parameter.ParametersDefinition? = null,
): T =
    getOrCreate {
        component.get<T>(qualifier, parameters)
    }

context(component: KoinComponent)
inline fun <reified T : InstanceKeeper.Instance> InstanceKeeper.getOrCreateKoin(
    key: Any,
    qualifier: org.koin.core.qualifier.Qualifier? = null,
    noinline parameters: org.koin.core.parameter.ParametersDefinition? = null,
): T =
    getOrCreate(key) {
        component.get<T>(qualifier, parameters)
    }

context(_: KoinComponent)
inline fun <reified T : InstanceKeeper.Instance> ComponentContext.getOrCreateKoin(
    qualifier: org.koin.core.qualifier.Qualifier? = null,
    noinline parameters: org.koin.core.parameter.ParametersDefinition? = null,
): T = instanceKeeper.getOrCreateKoin<T>(qualifier, parameters)

context(_: KoinComponent)
inline fun <reified T : InstanceKeeper.Instance> ComponentContext.getOrCreateKoin(
    key: Any,
    qualifier: org.koin.core.qualifier.Qualifier? = null,
    noinline parameters: org.koin.core.parameter.ParametersDefinition? = null,
): T = instanceKeeper.getOrCreateKoin<T>(key, qualifier, parameters)
