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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
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
import cz.lastaapps.menza.features.settings.domain.model.Currency
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.features.today.ui.util.dishContainerKey
import cz.lastaapps.menza.features.today.ui.util.dishImageKey
import cz.lastaapps.menza.features.today.ui.util.dishTitleKey
import cz.lastaapps.menza.features.today.ui.util.key
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.AnimationScopes
import cz.lastaapps.menza.ui.util.appCardColors
import cz.lastaapps.menza.ui.util.sharedBounds
import cz.lastaapps.menza.ui.util.sharedContainer
import cz.lastaapps.menza.ui.util.sharedElement
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
    scroll: LazyListState,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            modifier =
                Modifier
                    // required so the individual items are properly clipped
                    .sharedContainer(
                        scopes,
                        "today_dish_list",
                        clipInOverlayDuringTransitionShape = MaterialTheme.shapes.large,
                    ),
        ) {
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
                scopes = scopes,
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
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    // no data handling
    if (data.isEmpty()) {
        NoItems(onNoItems, modifier)
        return
    }

    // showing items
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
        state = scroll,
    ) {
        item(key = "header") {
            header(
                Modifier,
//                Modifier.animateItem(),
            )
        }

        data.forEach { category ->
            // TODO I'm not sure how to implement this while supporting shared element animations
            // stickyHeader(
            item(
                key = "header_" + category.key(),
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    DishHeader(
                        courseType = category,
                        modifier = Modifier.padding(bottom = Padding.Smaller),
                    )
                }
            }
            items(
                category.dishList,
                key = { it.key(category) },
            ) { dish ->
                DishItem(
                    dish = dish,
                    onDish = onDish,
                    onRating = onRating,
                    userSettings = userSettings,
                    isOnMetered = isOnMetered,
                    scopes = scopes,
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    // .animateItem(),
                )
            }
        }

        item(key = "footer") {
            footer(
                Modifier,
                // Modifier.animateItem(),
            )
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
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onDish(dish) },
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier =
            modifier.sharedContainer(
                scopes,
                dishContainerKey(dish.id),
                resizeMode = ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransitionShape = MaterialTheme.shapes.large,
            ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Padding.Tiny),
            modifier = Modifier.padding(Padding.MidSmall),
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
                        currency = userSettings.currency,
                        downloadOnMetered = userSettings.downloadOnMetered,
                        imageScale =
                            animateFloatAsState(
                                targetValue = userSettings.imageScale,
                                label = "image_scale",
                            ).value,
                        isOnMetered = isOnMetered,
                        modifier = Modifier.sharedElement(scopes, key = dishImageKey(dish.id)),
                    )

                    DishNameRow(
                        dish,
                        modifier = Modifier.sharedBounds(scopes, dishTitleKey(dish.id)),
                    )
                }
            } else {
                DishNameRow(
                    dish,
                    modifier = Modifier.sharedBounds(scopes, dishTitleKey(dish.id)),
                )
                DishBadgesRow(
                    dish = dish,
                    onRating = onRating,
                    priceType = userSettings.priceType,
                    currency = userSettings.currency,
                )
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
    currency: Currency,
    downloadOnMetered: Boolean,
    imageScale: Float,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
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
            currency = currency,
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
