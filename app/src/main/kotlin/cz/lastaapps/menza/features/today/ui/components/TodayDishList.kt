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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TodayDishList(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    showCzech: ShowCzech,
    imageScale: Float,
    isOnMetered: Boolean,
    gridSwitch: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scroll: LazyListState = rememberLazyListState(),
    pullState: PullRefreshState = rememberPullRefreshState(
        refreshing = isLoading, onRefresh = onRefresh,
    ),
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
                imageScale = imageScale,
                isOnMetered = isOnMetered,
                scroll = scroll,
                gridSwitch = gridSwitch,
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
    imageScale: Float,
    isOnMetered: Boolean,
    scroll: LazyListState,
    gridSwitch: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {

    //no data handling
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(MenzaPadding.Medium)) {
        // showing items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MenzaPadding.MidSmall),
            state = scroll,
        ) {
            data.forEach { category ->
                stickyHeader {
                    Surface(Modifier.fillMaxWidth()) {
                        DishHeader(
                            courseType = category,
                            showCzech = showCzech,
                            modifier = Modifier.padding(bottom = MenzaPadding.Smaller),
                        )
                    }
                }
                items(category.dishList) { dish ->
                    DishItem(
                        dish = dish,
                        onDishSelected = onDishSelected,
                        priceType = priceType,
                        downloadOnMetered = downloadOnMetered,
                        showCzech = showCzech,
                        imageScale = imageScale,
                        isOnMetered = isOnMetered,
                        modifier = Modifier.fillMaxWidth(),
                    )
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
    imageScale: Float,
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
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            DishImageWithBadge(
                dish = dish,
                priceType = priceType,
                downloadOnMetered = downloadOnMetered,
                imageScale = imageScale,
                isOnMetered = isOnMetered,
            )
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DishNameRow(dish, showCzech)
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
    imageScale: Float,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        DishImageWithPlaceholder(
            photoLink = dish.photoLink,
            downloadOnMetered = downloadOnMetered,
            imageScale = imageScale,
            isOnMetered = isOnMetered,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(
                    top = MenzaPadding.Small,
                    bottom = MenzaPadding.Small,
                    end = MenzaPadding.Small,
                )
        )
        DishBadge(
            dish = dish,
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun DishImageWithPlaceholder(
    photoLink: String?,
    downloadOnMetered: Boolean,
    imageScale: Float,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        val size = (96 * imageScale).dp
        val imageModifier = Modifier.size(size)


        if (photoLink != null) {
            DishImage(
                photoLink = photoLink,
                loadImmediately = downloadOnMetered || !isOnMetered,
                modifier = imageModifier,
            )

        } else {
            Box(imageModifier, contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = null,
                )
            }
        }
    }
}
