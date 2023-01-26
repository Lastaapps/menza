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

package cz.lastaapps.api.core.domain.model

import cz.lastaapps.api.core.domain.model.MenzaType.Strahov
import cz.lastaapps.api.core.domain.model.MenzaType.Subsystem
import cz.lastaapps.api.core.domain.model.common.Menza
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

// All the types must have unique names, or the DI will break
sealed interface MenzaType {
    data class Subsystem(val subsystemId: Int) : MenzaType
    data object Strahov : MenzaType {
        val instance = Menza(
            Strahov,
            "Restaurace Strahov",
            isOpened = true,
            isImportant = true
        )
    }
}

@JvmInline
value class MenzaTypeScope(val scope: Scope)

// Is not atomic, won't break hopefully
class MenzaScopeStore : MutableMap<MenzaType, MenzaTypeScope> by HashMap()

val module = module {

    // Once global
    singleOf(::MenzaScopeStore)

    factory { (menza: MenzaType) ->
        val store = get<MenzaScopeStore>()
        store.getOrPut(menza) {
            when (menza) {
                Strahov ->
                    MenzaTypeScope(getKoin().createScope<Strahov>())
                is Subsystem ->
                    MenzaTypeScope(getKoin().createScope<Subsystem>())
            }
        }
    }

//    factory {(menza: MenzaType) ->
//        val scope = get<MenzaTypeScope> { parametersOf(menza) }
//        scope.scope.get<DishListRepo>()
//    }
//
//    // per module
//    scope<MenzaType.Strahov> {
//        scopedOf(::DishListRepoStrahovImpl) bind DishListRepo::class
//    }
//    scope<MenzaType.Subsystem> {
//        scoped {
//            val menza = get<MenzaType.Subsystem>()
//            DishListRepoSubsystemImpl(menza.subsystemId, get(), get(), get(), get())
//        } bind DishListRepo::class
//    }
}
