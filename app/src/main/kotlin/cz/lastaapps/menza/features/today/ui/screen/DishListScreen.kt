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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.today.ui.components.CompactViewSwitch
import cz.lastaapps.menza.features.today.ui.components.TodayDishGrid
import cz.lastaapps.menza.features.today.ui.components.TodayDishList
import cz.lastaapps.menza.features.today.ui.vm.DishListState
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.ui.HandleError
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun DishListScreen(
    onDishSelected: (Dish) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DishListViewModel = koinViewModel(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
    scrollState: LazyListState = rememberLazyListState(),
    scrollGridState: LazyGridState = rememberLazyGridState(),
) {
    DishListEffects(viewModel, hostState)

    val state by viewModel.flowState
    DishListContent(
        state = state,
        modifier = modifier,
        hostState = hostState,
        onRefresh = viewModel::reload,
        onNoItems = viewModel::openWebMenu,
        onCompactView = viewModel::setCompactView,
        onDishSelected = onDishSelected,
        scrollListState = scrollState,
        scrollGridState = scrollGridState,
    )
}

@Composable
private fun DishListEffects(
    viewModel: DishListViewModel,
    hostState: SnackbarHostState,
) {
    HandleAppear(viewModel)
    HandleError(viewModel, hostState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun DishListContent(
    state: DishListState,
    hostState: SnackbarHostState,
    onRefresh: () -> Unit,
    onNoItems: () -> Unit,
    onCompactView: (isCompact: Boolean) -> Unit,
    onDishSelected: (Dish) -> Unit,
    scrollListState: LazyListState,
    scrollGridState: LazyGridState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        modifier = modifier,
    ) { padding ->

        val gridSwitch: @Composable () -> Unit = {
            CompactViewSwitch(
                isCompact = state.isCompact,
                onCompactChange = onCompactView,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (state.isCompact) {
            TodayDishList(
                isLoading = state.isLoading,
                onRefresh = onRefresh,
                data = state.items,
                onNoItems = onNoItems,
                onDishSelected = onDishSelected,
                priceType = state.priceType,
                downloadOnMetered = state.downloadOnMetered,
                showCzech = state.showCzech,
                imageScale = state.imageScale,
                isOnMetered = state.isOnMetered,
                gridSwitch = gridSwitch,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                scroll = scrollListState,
            )
        } else {
            TodayDishGrid(
                isLoading = state.isLoading,
                onRefresh = onRefresh,
                data = state.items,
                onNoItems = onNoItems,
                onDishSelected = onDishSelected,
                priceType = state.priceType,
                downloadOnMetered = state.downloadOnMetered,
                showCzech = state.showCzech,
                isOnMetered = state.isOnMetered,
                gridSwitch = gridSwitch,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                scrollGrid = scrollGridState,
            )
        }
    }
}
