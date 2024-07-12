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

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.COMPACT
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.GRID
import cz.lastaapps.menza.features.settings.domain.model.DishListMode.HORIZONTAL
import cz.lastaapps.menza.features.today.ui.vm.DishListState
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.widget.DishListViewModeSwitch
import cz.lastaapps.menza.features.today.ui.widget.Experimental
import cz.lastaapps.menza.features.today.ui.widget.ImageSizeSetting
import cz.lastaapps.menza.features.today.ui.widget.TodayDishGrid
import cz.lastaapps.menza.features.today.ui.widget.TodayDishHorizontal
import cz.lastaapps.menza.features.today.ui.widget.TodayDishList
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.HandleError
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun DishListScreen(
    onDishSelected: (Dish) -> Unit,
    onVideoLink: (String) -> Unit,
    viewModel: DishListViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    scrollStates: ScrollStates,
) {
    DishListEffects(viewModel, hostState)

    val lifecycle = LocalLifecycleOwner.current
    LaunchedEffect(lifecycle, viewModel) {
        lifecycle.lifecycle.currentStateFlow.collectLatest {
            viewModel.setIsResumed(it == Lifecycle.State.RESUMED)
        }
    }

    val state by viewModel.flowState
    DishListContent(
        state = state,
        onVideoLink = onVideoLink,
        modifier = modifier,
        onRefresh = viewModel::reload,
        onNoItems = viewModel::openWebMenu,
        onViewMode = viewModel::setCompactView,
        onImageScale = viewModel::setImageScale,
        onOliverRow = viewModel::setOliverRow,
        onDishSelected = onDishSelected,
        scrollStates = scrollStates,
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
private fun DishListContent(
    state: DishListState,
    onVideoLink: (String) -> Unit,
    onRefresh: () -> Unit,
    onNoItems: () -> Unit,
    onViewMode: (mode: DishListMode) -> Unit,
    onImageScale: (Float) -> Unit,
    onDishSelected: (Dish) -> Unit,
    onOliverRow: (Boolean) -> Unit,
    scrollStates: ScrollStates,
    modifier: Modifier = Modifier,
) = Column {
    val userSettings = state.userSettings
    val gridSwitch: @Composable () -> Unit = {
        DishListViewModeSwitch(
            currentMode = userSettings.dishListMode,
            onModeChange = onViewMode,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    val imageSizeSetting: @Composable () -> Unit = {
        ImageSizeSetting(
            progress = userSettings.imageScale,
            onProgressChanged = onImageScale,
            modifier = Modifier.fillMaxWidth(),
        )
    }
    val footerFabPadding: @Composable () -> Unit = {
        state.selectedMenza?.getOrNull()?.videoLinks?.firstOrNull()?.let {
            Spacer(Modifier.height(96.dp))
        }
    }

    val header: @Composable (Modifier) -> Unit = { modifier ->
        if (state.showExperimentalWarning) {
            Experimental(
                modifier
                    .fillMaxWidth()
                    .padding(bottom = Padding.Small),
            )
        }
    }

    Crossfade(
        targetState = userSettings.dishListMode,
        label = "dish_list_mode_router",
    ) { dishListMode ->
        Scaffold(
            floatingActionButton = {
                state.selectedMenza?.getOrNull()?.videoLinks?.firstOrNull()?.let { link ->
                    LiveVideoFeedFab(link = link, onVideoLink = onVideoLink)
                }
            },
        ) {
            when (dishListMode) {
                COMPACT ->
                    TodayDishList(
                        isLoading = state.isLoading,
                        onRefresh = onRefresh,
                        data = state.items,
                        onNoItems = onNoItems,
                        onDishSelected = onDishSelected,
                        userSettings = userSettings,
                        isOnMetered = state.isOnMetered,
                        header = header,
                        footer = { modifier: Modifier ->
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
                                modifier = modifier.fillMaxWidth(),
                            ) {
                                gridSwitch()

                                imageSizeSetting()

                                footerFabPadding()
                            }
                        },
                        modifier = modifier.fillMaxSize(),
                        scroll = scrollStates.list,
                    )

                GRID ->
                    TodayDishGrid(
                        isLoading = state.isLoading,
                        onRefresh = onRefresh,
                        data = state.items,
                        onNoItems = onNoItems,
                        onDishSelected = onDishSelected,
                        userSettings = userSettings,
                        isOnMetered = state.isOnMetered,
                        header = header,
                        footer = {
                            Column {
                                gridSwitch()
                                footerFabPadding()
                            }
                        },
                        modifier = modifier.fillMaxSize(),
                        scrollGrid = scrollStates.grid,
                    )

                HORIZONTAL ->
                    TodayDishHorizontal(
                        isLoading = state.isLoading,
                        onRefresh = onRefresh,
                        data = state.items,
                        onNoItems = onNoItems,
                        onDishSelected = onDishSelected,
                        userSettings = userSettings,
                        isOnMetered = state.isOnMetered,
                        onOliverRow = onOliverRow,
                        header = header,
                        footer = {
                            Column {
                                gridSwitch()
                                footerFabPadding()
                            }
                        },
                        modifier = modifier.fillMaxSize(),
                        scroll = scrollStates.horizontal,
                    )

                null -> {}
            }
        }
    }
}

@Composable
private fun LiveVideoFeedFab(
    link: String,
    onVideoLink: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val size = 64.dp
    FloatingActionButton(
        onClick = { onVideoLink(link) },
        modifier = modifier.size(size),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
    ) {
        Icon(
            Icons.Default.Videocam,
            stringResource(id = R.string.today_list_video_fab_content_description),
            modifier = Modifier.size(size / 2),
        )
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
) {
    companion object {
        val Saver: Saver<ScrollStates, *> = listSaver(
            save = {
                listOf(
                    with(LazyListState.Saver) { save(it.list) },
                    with(LazyStaggeredGridState.Saver) { save(it.grid) },
                    with(LazyListState.Saver) { save(it.horizontal) },
                )
            },
            restore = { list ->
                @Suppress("UNCHECKED_CAST")
                val llsSaver = LazyListState.Saver as Saver<LazyListState, Any>
                ScrollStates(
                    list[0]?.let { llsSaver.restore(it) }!!,
                    list[1]?.let { LazyStaggeredGridState.Saver.restore(it) }!!,
                    list[2]?.let { llsSaver.restore(it) }!!,
                )
            },
        )
    }
}