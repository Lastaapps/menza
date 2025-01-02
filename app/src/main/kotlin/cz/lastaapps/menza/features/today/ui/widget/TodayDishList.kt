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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun TodayDishList(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDish: (Dish) -> Unit,
    onRating: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    scroll: LazyListState = rememberLazyListState(),
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Surface(shape = MaterialTheme.shapes.large) {
            DishContent(
                data = data,
                onDish = onDish,
                onNoItems = onNoItems,
                onRating = onRating,
                userSettings = userSettings,
                isOnMetered = isOnMetered,
                scroll = scroll,
                header = header,
                footer = footer,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("ktlint:compose:modifier-reused-check")
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDish: (Dish) -> Unit,
    onNoItems: () -> Unit,
    onRating: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    scroll: LazyListState,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    // no data handling
    if (data.isEmpty()) {
        NoItems(onNoItems, modifier)
        return
    }

    // showing items
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
        state = scroll,
    ) {
        item(key = "header") {
            header(Modifier.animateItem())
        }

        data.forEach { category ->
            stickyHeader {
                Surface(Modifier.fillMaxWidth()) {
                    DishHeader(
                        courseType = category,
                        modifier = Modifier.padding(bottom = Padding.Smaller),
                    )
                }
            }
            items(
                category.dishList,
                key = { "" + category.name + it.name },
            ) { dish ->
                DishItem(
                    dish = dish,
                    onDish = onDish,
                    onRating = onRating,
                    userSettings = userSettings,
                    isOnMetered = isOnMetered,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .animateItem(),
                )
            }
        }

        item(key = "footer") {
            footer(Modifier.animateItem())
        }
    }

    // When the scale is changed, scrolls to the last element
    var latestScale by remember {
        mutableFloatStateOf(userSettings.imageScale)
    }
    LaunchedEffect(userSettings.imageScale) {
        if (latestScale != userSettings.imageScale) {
            latestScale = userSettings.imageScale
            val placedItems =
                2 + // header, footer
                    data.size + // sticky headers
                    data.sumOf { it.dishList.size } // items
            // as the other nodes are resizing, we need to wait for them to (almost) stop
            delay(666.milliseconds)
            scroll.animateScrollToItem(placedItems)
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDish: (Dish) -> Unit,
    onRating: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.clickable { onDish(dish) },
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            verticalArrangement = Arrangement.spacedBy(Padding.Tiny),
        ) {
            if (dish.photoLink != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                ) {
                    DishImageWithBadge(
                        dish = dish,
                        onRating = onRating,
                        priceType = userSettings.priceType,
                        downloadOnMetered = userSettings.downloadOnMetered,
                        imageScale =
                            animateFloatAsState(
                                targetValue = userSettings.imageScale,
                                label = "image_scale",
                            ).value,
                        isOnMetered = isOnMetered,
                    )

                    DishNameRow(dish)
                }
            } else {
                DishNameRow(dish)
                DishBadgesRow(dish = dish, onRating = onRating, priceType = userSettings.priceType)
            }
            DishInfoRow(dish)
        }
    }
}

@Composable
private fun DishImageWithBadge(
    dish: Dish,
    onRating: (Dish) -> Unit,
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
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(
                        top = Padding.Small,
                        bottom = Padding.Small,
                        end = Padding.Small,
                    ),
        )
        DishBadgesColumn(
            dish = dish,
            onRating = onRating,
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
