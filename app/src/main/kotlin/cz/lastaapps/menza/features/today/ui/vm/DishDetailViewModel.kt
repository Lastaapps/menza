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

package cz.lastaapps.menza.features.today.ui.vm

import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.main.domain.usecase.GetDishUC
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class DishDetailViewModel(
    context: VMContext,
    private val dish: DishOriginDescriptor,
    dishInitial: Dish?,
    private val getDishUC: GetDishUC,
) : StateViewModel<DishDetailState>(DishDetailState(dish = dishInitial), context) {
    override suspend fun whileSubscribed(scope: CoroutineScope) {
        getDishUC(dish)
            .onEach {
                updateState { copy(dish = it) }
            }.launchIn(scope)
    }
}

internal data class DishDetailState(
    val dish: Dish? = null,
) : VMState
