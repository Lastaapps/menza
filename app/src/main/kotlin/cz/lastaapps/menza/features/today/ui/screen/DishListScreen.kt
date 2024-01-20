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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.COMPACT
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.GRID
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.HORIZONTAL
import cz.lastaapps.menza.features.settings.ui.widget.ImageSizeSetting
import cz.lastaapps.menza.features.today.ui.vm.DishListState
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.widget.CompactViewSwitch
import cz.lastaapps.menza.features.today.ui.widget.Experimental
import cz.lastaapps.menza.features.today.ui.widget.TodayDishGrid
import cz.lastaapps.menza.features.today.ui.widget.TodayDishHorizontal
import cz.lastaapps.menza.features.today.ui.widget.TodayDishList
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.HandleError

@Composable
internal fun DishListScreen(
    onDishSelected: (Dish) -> Unit,
    viewModel: DishListViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    scrollState: LazyListState = rememberLazyListState(),
    scrollGridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
) {
    DishListEffects(viewModel, hostState)

    val state by viewModel.flowState
    DishListContent(
        state = state,
        modifier = modifier,
        onRefresh = viewModel::reload,
        onNoItems = viewModel::openWebMenu,
        onViewMode = viewModel::setCompactView,
        onImageScale = viewModel::setImageScale,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DishListContent(
    state: DishListState,
    onRefresh: () -> Unit,
    onNoItems: () -> Unit,
    onViewMode: (mode: DishListMode) -> Unit,
    onImageScale: (Float) -> Unit,
    onDishSelected: (Dish) -> Unit,
    scrollListState: LazyListState,
    scrollGridState: LazyStaggeredGridState,
    modifier: Modifier = Modifier,
) {
    Column {
        val gridSwitch: @Composable () -> Unit = {
            CompactViewSwitch(
                currentMode = state.dishListMode,
                onCompactChange = onViewMode,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        val imageSizeSetting: @Composable () -> Unit = {
            ImageSizeSetting(
                progress = state.imageScale,
                onProgressChanged = onImageScale,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        val header: @Composable () -> Unit = {
            if (state.showExperimentalWarning) {
                Experimental(
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = Padding.Small),
                )
            }
        }

        when (state.dishListMode) {
            COMPACT ->
                TodayDishList(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDishSelected = onDishSelected,
                    priceType = state.priceType,
                    downloadOnMetered = state.downloadOnMetered,
                    language = state.language,
                    imageScale = state.imageScale,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            gridSwitch()

                            OutlinedCard(modifier = Modifier.padding(horizontal = Padding.MidSmall)) {
                                Box(modifier = Modifier.padding(Padding.Medium)) {
                                    imageSizeSetting()
                                }
                            }
                        }
                    },
                    modifier = modifier.fillMaxSize(),
                    scroll = scrollListState,
                )

            GRID ->
                TodayDishGrid(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDishSelected = onDishSelected,
                    priceType = state.priceType,
                    downloadOnMetered = state.downloadOnMetered,
                    language = state.language,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = gridSwitch,
                    modifier = modifier.fillMaxSize(),
                    scrollGrid = scrollGridState,
                )

            HORIZONTAL ->
                TodayDishHorizontal(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDishSelected = onDishSelected,
                    priceType = state.priceType,
                    downloadOnMetered = state.downloadOnMetered,
                    language = state.language,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = gridSwitch,
                    modifier = modifier.fillMaxSize(),
                    scroll = scrollListState,
                )

            null -> {}
        }
    }
}
