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

package cz.lastaapps.menza.features.settings.ui.vm

import arrow.core.Either
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetDishLanguageUC

internal class DishLanguageViewModel(
    context: VMContext,
    private val setDishLanguageUC: SetDishLanguageUC,
) : StateViewModel<DishLanguageState>(DishLanguageState(), context) {
    fun selectLanguage(language: DataLanguage) =
        launchVM {
            withLoading({ copy(loading = it) }) {
                val res = setDishLanguageUC(language)
                updateState {
                    when (res) {
                        is Either.Left -> copy(error = res.value)
                        is Either.Right -> copy(isSelected = true)
                    }
                }
            }
        }

    fun dismiss() {
        updateState { DishLanguageState() }
    }
}

internal data class DishLanguageState(
    val loading: Boolean = false,
    val error: DomainError? = null,
    val isSelected: Boolean = false,
) : VMState
