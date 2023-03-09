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

package cz.lastaapps.menza.features.starting.ui.vm

import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.usecase.GetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetPriceTypeUC
import kotlinx.coroutines.flow.first

internal class PriceTypeViewModel internal constructor(
    context: VMContext,
    private val getPriceType: GetPriceTypeUC,
    private val setPriceType: SetPriceTypeUC,
) : StateViewModel<PriceTypeState>(PriceTypeState(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() = launchVM {
        getPriceType()
            .first()
            .let { type ->
                if (type != PriceType.Unset) {
                    updateState { copy(isReady = true, isSelected = true) }
                } else {
                    updateState { copy(isReady = true) }
                }
            }
    }

    fun selectType(type: PriceType) = launchVM {
        setPriceType(type)
        updateState { copy(isSelected = true) }
    }

    fun dismissSelected() = updateState { copy(isSelected = false) }
}

internal data class PriceTypeState(
    val isReady: Boolean = false,
    val isSelected: Boolean = false,
) : VMState
