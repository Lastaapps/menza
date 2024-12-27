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

import arrow.core.Option
import arrow.core.toOption
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.rating.domain.usecase.UpdateDishRatingUC
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetDishLanguageUC
import cz.lastaapps.menza.features.today.ui.model.DishForRating
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach

internal class TodayViewModel(
    context: VMContext,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getDishLanguageUC: GetDishLanguageUC,
    private val getUpdateDishRatingUC: UpdateDishRatingUC,
) : StateViewModel<TodayState>(TodayState(), context) {
    override suspend fun whileSubscribed(scope: CoroutineScope) {
        getSelectedMenza()
            .onEach {
                updateState {
                    copy(
                        selectedMenza = it.toOption(),
                        selectedDish = null,
                    )
                }
            }.launchIn(scope)

        getDishLanguageUC()
            .onEach {
                updateState { copy(language = it) }
            }.launchIn(scope)

        // This makes sure the selected dish is being updated
        // when rating changes
        flow
            .map { it.selectedDish }
            .distinctUntilChangedBy { it?.id }
            .mapLatest { dish ->
                dish?.let {
                    getUpdateDishRatingUC(dish).collectLatest { dish ->
                        updateState { copy(selectedDish = dish) }
                    }
                }
            }.launchIn(scope)
    }

    fun selectDish(dish: Dish?) = updateState { copy(selectedDish = dish) }

    fun convertDish(dish: Dish) =
        launchVM {
            updateState { copy(dishForRating = DishForRating.from(dish)) }
        }

    fun dismissDishForRating() = updateState { copy(dishForRating = null) }
}

internal data class TodayState(
    val selectedMenza: Option<Menza>? = null,
    val selectedDish: Dish? = null,
    val language: DataLanguage = DataLanguage.Czech,
    val dishForRating: DishForRating? = null,
) : VMState {
    val hasDish: Boolean get() = selectedDish != null
}
