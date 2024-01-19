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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.widgets.WrapMenzaNotSelected
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.vm.TodayState
import cz.lastaapps.menza.features.today.ui.vm.TodayViewModel
import cz.lastaapps.menza.features.today.ui.widget.NoDishSelected
import cz.lastaapps.menza.features.today.ui.widget.TodayInfo
import cz.lastaapps.menza.ui.components.BackArrow
import cz.lastaapps.menza.ui.components.layout.TwoPaneLayout
import cz.lastaapps.menza.ui.theme.Padding

@Composable
internal fun TodayScreen(
    onOsturak: () -> Unit,
    panels: @Composable (Modifier) -> Unit,
    viewModel: TodayViewModel,
    dishListViewModel: DishListViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    TodayEffects(viewModel)

    val state by viewModel.flowState
    TodayContent(
        state = state,
        onOsturak = onOsturak,
        onDishSelected = viewModel::selectDish,
        panels = panels,
        dishListViewModel = dishListViewModel,
        hostState = hostState,
        modifier = modifier,
    )
}

@Composable
private fun TodayEffects(
    viewModel: TodayViewModel,
) {
    HandleAppear(viewModel)

    val state by viewModel.flowState
    BackArrow(enabled = state.hasDish) {
        viewModel.selectDish(null)
    }
}

@Composable
private fun TodayContent(
    state: TodayState,
    onDishSelected: (Dish) -> Unit,
    onOsturak: () -> Unit,
    panels: @Composable (Modifier) -> Unit,
    dishListViewModel: DishListViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    scrollGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val dishList: @Composable () -> Unit = {
        DishListScreen(
            onDishSelected = onDishSelected,
            viewModel = dishListViewModel,
            modifier = Modifier.fillMaxSize(),
            hostState = hostState,
            scrollState = scrollState,
            scrollGridState = scrollGridState,
        )
    }

    val dishDetail: @Composable () -> Unit = {
        Crossfade(
            targetState = state.selectedDish,
            label = "dish_detail",
        ) { currentDish ->
            currentDish?.let {
                TodayInfo(
                    dish = currentDish,
                    showCzech = state.showCzech,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    val dishNone: @Composable () -> Unit = {
        NoDishSelected(Modifier.fillMaxSize())
    }

    WrapMenzaNotSelected(
        menza = state.selectedMenza,
        onOsturak = onOsturak,
        modifier = modifier,
    ) {
        Column {
            TwoPaneLayout(
                showDetail = state.hasDish,
                listNode = dishList,
                detailNode = dishDetail,
                emptyNode = dishNone,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

            panels(
                Modifier
                    .fillMaxWidth()
                    .padding(top = Padding.Medium),
            )
        }
    }
}
