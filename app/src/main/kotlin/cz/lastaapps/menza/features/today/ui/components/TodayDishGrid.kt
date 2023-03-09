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

package cz.lastaapps.menza.features.today.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun TodayDishGrid(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    showCzech: ShowCzech,
    isOnMetered: Boolean,
    gridSwitch: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollGrid: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    pullState: PullRefreshState = rememberPullRefreshState(
        refreshing = isLoading, onRefresh = onRefresh,
    ),
    widthSize: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .pullRefresh(pullState),
    ) {
        Surface(shape = MaterialTheme.shapes.large) {
            DishContent(
                data = data,
                onDishSelected = onDishSelected,
                onNoItems = onNoItems,
                priceType = priceType,
                downloadOnMetered = downloadOnMetered,
                showCzech = showCzech,
                isOnMetered = isOnMetered,
                scroll = scrollGrid,
                gridSwitch = gridSwitch,
                widthSize = widthSize,
                modifier = Modifier
                    .padding(top = MenzaPadding.Smaller) // so text is not cut off
                    .fillMaxSize(),
            )
        }

        MaterialPullIndicatorAligned(isLoading, pullState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDishSelected: (Dish) -> Unit,
    onNoItems: () -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    showCzech: ShowCzech,
    isOnMetered: Boolean,
    scroll: LazyStaggeredGridState,
    gridSwitch: @Composable () -> Unit,
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(MenzaPadding.Medium)) {
        val columns =
            if (widthSize == WindowWidthSizeClass.Compact) {
                StaggeredGridCells.Fixed(1)
            } else {
                StaggeredGridCells.Adaptive(172.dp)
            }

        LazyVerticalStaggeredGrid(
            columns = columns,
            modifier = Modifier.weight(1f),
            verticalItemSpacing = MenzaPadding.MidSmall,
            horizontalArrangement = Arrangement.spacedBy(MenzaPadding.MidSmall),
            state = scroll,
        ) {
            data.forEach { category ->
                itemsIndexed(category.dishList) { index, dish ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
                    ) {
                        if (index == 0) {
                            DishHeader(
                                courseType = category,
                                showCzech = showCzech,
                            )
                        }
                        DishItem(
                            dish = dish,
                            onDishSelected = onDishSelected,
                            priceType = priceType,
                            downloadOnMetered = downloadOnMetered,
                            showCzech = showCzech,
                            isOnMetered = isOnMetered,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item {
                gridSwitch()
            }
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    showCzech: ShowCzech,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        Column(
            Modifier.padding(MenzaPadding.MidSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
        ) {

            if (dish.photoLink != null) {
                DishImageWithBadge(
                    dish = dish,
                    priceType = priceType,
                    downloadOnMetered = downloadOnMetered,
                    isOnMetered = isOnMetered,
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MenzaPadding.Smaller),
                ) {
                    DishNameRow(
                        dish = dish,
                        showCzech = showCzech,
                        modifier = modifier.weight(1f),
                    )
                    if (dish.photoLink == null) {
                        DishBadge(dish = dish, priceType = priceType)
                    }
                }

                DishInfoRow(dish, showCzech)
            }
        }
    }
}

@Composable
private fun DishImageWithBadge(
    dish: Dish,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        DishImage(
            dish = dish,
            downloadOnMetered = downloadOnMetered,
            isOnMetered = isOnMetered,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(MenzaPadding.Small)
        )
        DishBadge(
            dish = dish,
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun DishImage(
    dish: Dish,
    downloadOnMetered: Boolean,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        val imageModifier = Modifier
            .fillMaxWidth()
//            .aspectRatio(16f / 9f)
            .aspectRatio(4f / 3f)

        if (dish.photoLink != null) {

            var retryHash by remember { mutableStateOf(0) }
            var userAllowed by rememberSaveable(dish.photoLink) { mutableStateOf(false) }
            val canDownload = remember(isOnMetered, userAllowed) {
                downloadOnMetered || !isOnMetered || userAllowed
            }

            val imageRequest = with(ImageRequest.Builder(LocalContext.current)) {
                diskCacheKey(dish.photoLink)
                memoryCacheKey(dish.photoLink)
                crossfade(true)
                setParameter("retry_hash", retryHash)
                // if user is not on a metered network, images are going to be loaded from cache
                if (canDownload)
                    data(dish.photoLink)
                else
                    data("https://userisonmeterednetwork.localhost/")
                //data(null) - cache is not working
                build()
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                modifier = imageModifier,
            ) {
                SubcomposeAsyncImage(
                    imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            imageModifier
                                .placeholder(
                                    true, color = MaterialTheme.colorScheme.secondary,
                                    shape = MaterialTheme.shapes.medium,
                                    highlight = PlaceholderHighlight.fade(
                                        highlightColor = MaterialTheme.colorScheme.primary,
                                    )
                                )
                                .clickable { retryHash++ }
                        )
                    },
                    error = {
                        Box(
                            imageModifier.clickable { retryHash++; userAllowed = true },
                            contentAlignment = Alignment.Center,
                        ) {
                            if (canDownload)
                                Icon(
                                    Icons.Default.Refresh,
                                    stringResource(R.string.today_list_image_load_failed)
                                )
                            else
                                Icon(
                                    Icons.Default.Download,
                                    stringResource(R.string.today_list_image_metered)
                                )
                        }
                    },
                )
            }
        }
    }
}
