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

package cz.lastaapps.menza.features.today.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.components.WrapMenzaNotSelected
import cz.lastaapps.menza.features.today.ui.components.NoDishSelected
import cz.lastaapps.menza.features.today.ui.components.TodayInfo
import cz.lastaapps.menza.features.today.ui.vm.TodayState
import cz.lastaapps.menza.features.today.ui.vm.TodayViewModel
import cz.lastaapps.menza.ui.components.layout.TwoPaneLayout
import cz.lastaapps.menza.ui.root.BackArrow
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun TodayScreen(
    onOsturak: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = koinViewModel(),
) {
    TodayEffects(viewModel)

    val state by viewModel.flowState
    TodayContent(
        state = state,
        onOsturak = onOsturak,
        onDishSelected = viewModel::selectDish,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TodayContent(
    state: TodayState,
    onDishSelected: (Dish) -> Unit,
    onOsturak: () -> Unit,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    scrollGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    val dishList: @Composable () -> Unit = {
        DishListScreen(
            onDishSelected = onDishSelected,
            modifier = Modifier.fillMaxSize(),
            scrollState = scrollState,
            scrollGridState = scrollGridState,
        )
    }

    val dishDetail: @Composable () -> Unit = {
        Crossfade(targetState = state.selectedDish) { currentDish ->
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
        TwoPaneLayout(
            showDetail = state.hasDish,
            listNode = dishList,
            detailNode = dishDetail,
            emptyNode = dishNone,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
