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

package cz.lastaapps.menza.features.main.ui.vm

import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.main.domain.usecase.SelectMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedVisibleMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.IsMenzaOrderFromTopUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

internal class MenzaSelectionViewModel(
    context: VMContext,
    private val getMenzaList: GetOrderedVisibleMenzaListUC,
    private val isMenzaOrderFromTop: IsMenzaOrderFromTopUC,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val selectMenza: SelectMenzaUC,
) : StateViewModel<MenzaSelectionState>(MenzaSelectionState(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() = launchVM {
        launchVM {
            getSelectedMenza().collectLatest {
                updateState { copy(selectedMenza = it) }
            }
        }
        launchVM {
            getMenzaList().collectLatest {
                updateState { copy(menzaList = it) }
            }
        }
        launchVM {
            isMenzaOrderFromTop().collectLatest {
                updateState { copy(fromTop = it) }
            }
        }
    }

    fun selectMenza(menza: Menza) = launchVM {
        selectMenza.invoke(menza)
    }
}

internal data class MenzaSelectionState(
    val selectedMenza: Menza? = null,
    val fromTop: Boolean = true,
    val menzaList: ImmutableList<Menza> = persistentListOf(),
)
