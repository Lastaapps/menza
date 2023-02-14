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

package cz.lastaapps.menza.features.settings.ui.vm

import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.menza.features.settings.domain.model.MenzaOrder
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.IsMenzaOrderFromTopUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.SetMenzaOrderFromTopUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.ToggleMenzaVisibilityUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.UpdateMenzaOrderUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal class ReorderMenzaViewModel(
    context: VMContext,
    private val toggleVisibility: ToggleMenzaVisibilityUC,
    private val getOrderedMenzaList: GetOrderedMenzaListUC,
    private val updateMenzaOrder: UpdateMenzaOrderUC,
    private val isMenzaOrderFromTop: IsMenzaOrderFromTopUC,
    private val setMenzaOrderFromTop: SetMenzaOrderFromTopUC,
) : StateViewModel<ReorderMenzaState>(ReorderMenzaState(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() = launch {
        launch {
            getOrderedMenzaList().collect {
                updateState { copy(menzaList = it) }
            }
        }
        launch {
            isMenzaOrderFromTop().collect {
                updateState { copy(fromTop = it) }
            }
        }
    }

    fun toggleVisibility(menza: Menza) = launch {
        toggleVisibility.invoke(menza)
    }

    fun saveOrder(items: List<Pair<Menza, MenzaOrder>>) = launch {
        updateMenzaOrder(items.map { (menza, order) -> menza to order.visible })
    }

    fun reverseOrder() = launch {
        setMenzaOrderFromTop(!lastState().fromTop)
    }
}

internal data class ReorderMenzaState(
    val fromTop: Boolean = true,
    val menzaList: ImmutableList<Pair<Menza, MenzaOrder>> = persistentListOf(),
)
