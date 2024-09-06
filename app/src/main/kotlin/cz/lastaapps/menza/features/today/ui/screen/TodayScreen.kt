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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy.DISABLED
import coil3.request.ImageRequest.Builder
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.widgets.WrapMenzaNotSelected
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.vm.TodayState
import cz.lastaapps.menza.features.today.ui.vm.TodayViewModel
import cz.lastaapps.menza.features.today.ui.widget.NoDishSelected
import cz.lastaapps.menza.features.today.ui.widget.TodayInfo
import cz.lastaapps.menza.ui.components.BackArrow
import cz.lastaapps.menza.ui.components.BaseDialog
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
        onDish = viewModel::selectDish,
        panels = panels,
        dishListViewModel = dishListViewModel,
        hostState = hostState,
        modifier = modifier,
    )
}

@Composable
private fun TodayEffects(viewModel: TodayViewModel) {
    HandleAppear(viewModel)

    val state by viewModel.flowState
    BackArrow(enabled = state.hasDish) {
        viewModel.selectDish(null)
    }
}

@Composable
private fun TodayContent(
    state: TodayState,
    onDish: (Dish) -> Unit,
    onOsturak: () -> Unit,
    panels: @Composable (Modifier) -> Unit,
    dishListViewModel: DishListViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    // resets scroll position when new menza is selected
    scrollStates: ScrollStates =
        rememberSaveable(
            state.selectedMenza,
            saver = ScrollStates.Saver,
        ) { ScrollStates() },
) {
    var videoFeedUrl by remember(state.selectedMenza) {
        mutableStateOf<String?>(null)
    }

    val dishList: @Composable () -> Unit = {
        DishListScreen(
            onDish = onDish,
            onVideoLink = { videoFeedUrl = it },
            viewModel = dishListViewModel,
            modifier = Modifier.fillMaxSize(),
            hostState = hostState,
            scrollStates = scrollStates,
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
                    onRating = { }, // TODO
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
                modifier =
                    Modifier
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

    videoFeedUrl?.let {
        ImagePreviewDialog(videoFeedUrl = it) {
            videoFeedUrl = null
        }
    }
}

@Composable
private fun ImagePreviewDialog(
    videoFeedUrl: String,
    onDismissRequest: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
    ) {
        val imageRequest =
            with(Builder(LocalContext.current)) {
                diskCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                data(videoFeedUrl)
                build()
            }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                Icon(Icons.Default.Videocam, null)
                Text(
                    text = stringResource(id = R.string.today_list_video_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
            }

            SubcomposeAsyncImage(
                imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                loading = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    Padding.Small,
                                    Alignment.CenterHorizontally,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.ErrorOutline, null)
                            Text(stringResource(id = R.string.today_list_video_error))
                        }
                    }
                },
                modifier = Modifier.aspectRatio(4f / 3f),
            )
        }
    }
}
