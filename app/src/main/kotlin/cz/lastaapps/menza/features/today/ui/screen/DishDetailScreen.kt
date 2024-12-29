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

package cz.lastaapps.menza.features.today.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.menza.features.today.ui.vm.DishDetailState
import cz.lastaapps.menza.features.today.ui.vm.DishDetailViewModel
import cz.lastaapps.menza.features.today.ui.widget.TodayDishDetail

@Composable
internal fun DishDetailScreen(
    viewModel: DishDetailViewModel,
    onRating: (DishOriginDescriptor) -> Unit,
    modifier: Modifier = Modifier,
) {
    DishDetailEffects(viewModel)

    val state by viewModel.flowState
    DishDetailContent(
        state = state,
        onRating = onRating,
        modifier = modifier,
    )
}

@Suppress("UNUSED_PARAMETER")
@Composable
private fun DishDetailEffects(viewModel: DishDetailViewModel) {
}

@Composable
private fun DishDetailContent(
    state: DishDetailState,
    onRating: (DishOriginDescriptor) -> Unit,
    modifier: Modifier = Modifier,
) {
    state.dish?.let { dish ->
        TodayDishDetail(
            dish = dish,
            onRating = { onRating(DishOriginDescriptor.from(it)) },
            modifier = modifier,
        )
    }
}
