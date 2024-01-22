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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TodayDishHorizontal(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    language: DishLanguage,
    isOnMetered: Boolean,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scroll: LazyListState = rememberLazyListState(),
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxWidth(),
    ) {
        Surface(shape = MaterialTheme.shapes.large) {
            DishContent(
                data = data,
                onDishSelected = onDishSelected,
                onNoItems = onNoItems,
                priceType = priceType,
                downloadOnMetered = downloadOnMetered,
                language = language,
                isOnMetered = isOnMetered,
                scroll = scroll,
                header = header,
                footer = footer,
                modifier = Modifier
                    .padding(top = Padding.Smaller) // so text is not cut off
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDishSelected: (Dish) -> Unit,
    onNoItems: () -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    language: DishLanguage,
    isOnMetered: Boolean,
    scroll: LazyListState,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {

    //no data handling
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(Padding.Medium)) {
        // showing items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
            state = scroll,
        ) {
            item {
                header()
            }

            data.forEach { category ->
                item {
                    DishHeader(
                        courseType = category,
                        language = language,
                        modifier = Modifier.padding(bottom = Padding.Smaller),
                    )
                }
                item {
                    val isOnlyItem = category.dishList.size == 1

                    if (isOnlyItem) {
                        DishItem(
                            dish = category.dishList.first(),
                            onDishSelected = onDishSelected,
                            priceType = priceType,
                            downloadOnMetered = downloadOnMetered,
                            language = language,
                            isOnMetered = isOnMetered,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(
                                Padding.MidLarge,
                                Alignment.CenterHorizontally,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize(),
                        ) {
                            items(category.dishList) { dish ->
                                DishItem(
                                    dish = dish,
                                    onDishSelected = onDishSelected,
                                    priceType = priceType,
                                    downloadOnMetered = downloadOnMetered,
                                    language = language,
                                    isOnMetered = isOnMetered,
                                    modifier = Modifier.sizeIn(maxWidth = 256.dp),
                                )
                            }
                        }
                    }
                }
            }

            item {
                footer()
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
    language: DishLanguage,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onDishSelected(dish) },
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
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
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Padding.Smaller),
                ) {
                    DishNameRow(
                        dish = dish,
                        language = language,
                        modifier = modifier.weight(1f),
                    )
                    if (dish.photoLink == null) {
                        DishBadge(
                            dish = dish,
                            priceType = priceType,
                        )
                    }
                }

                DishInfoRow(dish, language)
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
        dish.photoLink?.let { photoLink ->
            DishImageRatio(
                photoLink = photoLink,
                loadImmediately = downloadOnMetered || !isOnMetered,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(Padding.Small),
            )
        }
        DishBadge(
            dish = dish,
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}
