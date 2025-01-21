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

package cz.lastaapps.menza.features.today.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.menza.features.main.ui.widgets.WrapMenzaNotSelected
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.CAROUSEL
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.COMPACT
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.GRID
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.HORIZONTAL
import cz.lastaapps.menza.features.today.ui.vm.DishListState
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.widget.DishListViewModeSwitch
import cz.lastaapps.menza.features.today.ui.widget.Experimental
import cz.lastaapps.menza.features.today.ui.widget.ImageSizeSetting
import cz.lastaapps.menza.features.today.ui.widget.TodayDishCarousel
import cz.lastaapps.menza.features.today.ui.widget.TodayDishGrid
import cz.lastaapps.menza.features.today.ui.widget.TodayDishHorizontal
import cz.lastaapps.menza.features.today.ui.widget.TodayDishList
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.AnimationScopes
import cz.lastaapps.menza.ui.util.HandleError

@Composable
internal fun DishListScreen(
    viewModel: DishListViewModel,
    panels: @Composable (Modifier) -> Unit,
    onOsturak: () -> Unit,
    onDish: (Dish) -> Unit,
    onRating: (Dish) -> Unit,
    hostState: SnackbarHostState,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    DishListEffects(viewModel, hostState)

    val state by viewModel.flowState

    // resets scroll position when new menza is selected
    val scrollStates: ScrollStates =
        rememberSaveable(
            state.selectedMenza?.getOrNull(),
            saver = ScrollStates.Saver,
        ) { ScrollStates() }

    DishListContent(
        state = state,
        modifier = modifier,
        onRefresh = viewModel::reload,
        onNoItems = viewModel::openWebMenu,
        onViewMode = viewModel::setCompactView,
        onImageScale = viewModel::setImageScale,
        onOliverRow = viewModel::setOliverRow,
        onDish = onDish,
        onRating = onRating,
        onDismissDishListModeChooser = viewModel::dismissListModeChosen,
        panels = panels,
        onOsturak = onOsturak,
        scrollStates = scrollStates,
        scopes = scopes,
    )
}

@Composable
private fun DishListEffects(
    viewModel: DishListViewModel,
    hostState: SnackbarHostState,
) {
    HandleError(viewModel, hostState)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun DishListContent(
    state: DishListState,
    panels: @Composable (Modifier) -> Unit,
    onRefresh: () -> Unit,
    onNoItems: () -> Unit,
    onViewMode: (mode: DishListMode) -> Unit,
    onImageScale: (Float) -> Unit,
    onDish: (Dish) -> Unit,
    onOliverRow: (Boolean) -> Unit,
    onOsturak: () -> Unit,
    onRating: (Dish) -> Unit,
    onDismissDishListModeChooser: () -> Unit,
    scrollStates: ScrollStates,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        WrapMenzaNotSelected(
            menza = state.selectedMenza,
            onOsturak = onOsturak,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            DishListComposing(
                state = state,
                onDish = onDish,
                onRating = onRating,
                modifier = Modifier.fillMaxSize(),
                scrollStates = scrollStates,
                onRefresh = onRefresh,
                onNoItems = onNoItems,
                onViewMode = onViewMode,
                onImageScale = onImageScale,
                onOliverRow = onOliverRow,
                onDismissDishListModeChooser = onDismissDishListModeChooser,
                scopes = scopes,
            )
        }

        panels(
            Modifier
                .fillMaxWidth()
                .padding(top = Padding.Medium),
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun DishListComposing(
    state: DishListState,
    onRefresh: () -> Unit,
    onNoItems: () -> Unit,
    onViewMode: (mode: DishListMode) -> Unit,
    onImageScale: (Float) -> Unit,
    onDish: (Dish) -> Unit,
    onOliverRow: (Boolean) -> Unit,
    onRating: (Dish) -> Unit,
    onDismissDishListModeChooser: () -> Unit,
    scrollStates: ScrollStates,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) = Column {
    val userSettings = state.userSettings
    val gridSwitch: @Composable (isInHeader: Boolean) -> Unit = { isInHeader ->
        AnimatedVisibility(isInHeader != userSettings.isDishListModeChosen) {
            DishListViewModeSwitch(
                currentMode = userSettings.dishListMode,
                onModeChange = onViewMode,
                isDismissibleVisible = !userSettings.isDishListModeChosen,
                onDismiss = onDismissDishListModeChooser,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    val imageSizeSetting: @Composable () -> Unit = {
        ImageSizeSetting(
            progress = userSettings.imageScale,
            onProgressChange = onImageScale,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    val footerFabPadding: @Composable () -> Unit = {
        state.selectedMenza?.getOrNull()?.videoLinks?.firstOrNull()?.let {
            Spacer(Modifier.height(96.dp + Padding.MidSmall))
        }
    }

    val experimentalWarning: @Composable (Modifier) -> Unit = { modifier ->
        if (state.showExperimentalWarning) {
            Experimental(
                modifier
                    .fillMaxWidth()
                    .padding(bottom = Padding.Small),
            )
        }
    }

    val header: @Composable (Modifier) -> Unit = { modifier: Modifier ->
        Column(
            verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
            modifier = modifier.fillMaxWidth(),
        ) {
            experimentalWarning(Modifier)
            gridSwitch(true)
        }
    }

//    Crossfade(
//        targetState = userSettings.dishListMode,
//        label = "dish_list_mode_router",
//    ) { dishListMode ->
    userSettings.dishListMode.let { dishListMode ->
        when (dishListMode) {
            COMPACT ->
                TodayDishList(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDish = onDish,
                    onRating = onRating,
                    userSettings = userSettings,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = { modifier: Modifier ->
                        Column(
                            verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
                            modifier = modifier.fillMaxWidth(),
                        ) {
                            gridSwitch(false)

                            imageSizeSetting()

                            footerFabPadding()
                        }
                    },
                    modifier = modifier.fillMaxSize(),
                    scroll = scrollStates.list,
                    scopes = scopes,
                )

            GRID ->
                TodayDishGrid(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDish = onDish,
                    onRating = onRating,
                    userSettings = userSettings,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = {
                        Column {
                            gridSwitch(false)
                            footerFabPadding()
                        }
                    },
                    modifier = modifier.fillMaxSize(),
                    scrollGrid = scrollStates.grid,
                    scopes = scopes,
                )

            HORIZONTAL ->
                TodayDishHorizontal(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDish = onDish,
                    userSettings = userSettings,
                    isOnMetered = state.isOnMetered,
                    onOliverRow = onOliverRow,
                    header = header,
                    footer = {
                        Column {
                            gridSwitch(false)
                            footerFabPadding()
                        }
                    },
                    modifier = modifier.fillMaxSize(),
                    scroll = scrollStates.horizontal,
                    scopes = scopes,
                )

            CAROUSEL ->
                TodayDishCarousel(
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    data = state.items,
                    onNoItems = onNoItems,
                    onDish = onDish,
                    onRating = onRating,
                    userSettings = userSettings,
                    isOnMetered = state.isOnMetered,
                    header = header,
                    footer = {
                        Column {
                            gridSwitch(false)
                            footerFabPadding()
                        }
                    },
                    modifier = modifier.fillMaxSize(),
                    scroll = scrollStates.carousel,
                    scopes = scopes,
                )

            null -> {}
        }
    }
}

/**
 * Holds all the scroll states for different scroll modes.
 * The states must not be shared as some animations will get broken
 */
internal data class ScrollStates(
    val list: LazyListState = LazyListState(),
    val grid: LazyStaggeredGridState = LazyStaggeredGridState(),
    val horizontal: LazyListState = LazyListState(),
    val carousel: LazyListState = LazyListState(),
) {
    companion object {
        val Saver: Saver<ScrollStates, *> =
            listSaver(
                save = {
                    listOf(
                        with(LazyListState.Saver) { save(it.list) },
                        with(LazyStaggeredGridState.Saver) { save(it.grid) },
                        with(LazyListState.Saver) { save(it.horizontal) },
                        with(LazyListState.Saver) { save(it.carousel) },
                    )
                },
                restore = { list ->
                    @Suppress("UNCHECKED_CAST")
                    val llsSaver = LazyListState.Saver as Saver<LazyListState, Any>
                    ScrollStates(
                        list[0]?.let { llsSaver.restore(it) }!!,
                        list[1]?.let { LazyStaggeredGridState.Saver.restore(it) }!!,
                        list[2]?.let { llsSaver.restore(it) }!!,
                        list[3]?.let { llsSaver.restore(it) }!!,
                    )
                },
            )
    }
}
