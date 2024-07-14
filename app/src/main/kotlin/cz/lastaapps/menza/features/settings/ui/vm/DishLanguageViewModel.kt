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

package cz.lastaapps.menza.features.settings.ui.vm

import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetDishLanguageUC

internal class DishLanguageViewModel(
    context: VMContext,
    private val setDishLanguageUC: SetDishLanguageUC,
) : StateViewModel<DishLanguageState>(DishLanguageState(), context) {

    fun selectLanguage(language: DishLanguage) = launchVM {
        setDishLanguageUC(language)
        updateState { copy(isSelected = true) }
    }

    fun dismissSelected() {
        updateState { copy(isSelected = false) }
    }
}

internal data class DishLanguageState(
    val isSelected: Boolean = false,
) : VMState
