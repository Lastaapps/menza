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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TodayDishGrid(
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
    scrollGrid: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    widthSize: WindowWidthSizeClass = LocalWindowWidth.current,
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
                scroll = scrollGrid,
                header = header,
                footer = footer,
                widthSize = widthSize,
                modifier =
                Modifier
                    .padding(top = Padding.Smaller) // so text is not cut off
                    .fillMaxSize(),
            )
        }
    }
}

@Composable
@Suppress("ktlint:compose:modifier-reused-check")
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDish: (Dish) -> Unit,
    onNoItems: () -> Unit,
    onRating: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    scroll: LazyStaggeredGridState,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
) {
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(Padding.Medium)) {
        val columns =
            if (widthSize == WindowWidthSizeClass.Compact) {
                StaggeredGridCells.Fixed(1)
            } else {
                StaggeredGridCells.Adaptive(172.dp)
            }

        LazyVerticalStaggeredGrid(
            columns = columns,
            modifier = Modifier.weight(1f),
            verticalItemSpacing = Padding.MidSmall,
            horizontalArrangement = Arrangement.spacedBy(Padding.MidSmall),
            state = scroll,
        ) {
            // https://issuetracker.google.com/issues/321784348
            // This issue is technically fixed, but is is still not working
            // But I cannot reproduce it anymore
            item {
                Spacer(modifier = Modifier.height(1.dp))
            }

            item(key = "header") {
                header(Modifier.animateItem())
            }

            data.forEach { category ->
                itemsIndexed(
                    category.dishList,
                    key = { _, dish -> "" + category.name + dish.name },
                ) { index, dish ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Padding.Medium),
                        modifier = Modifier.animateItem(),
                    ) {
                        if (index == 0) {
                            DishHeader(courseType = category)
                        }
                        DishItem(
                            dish = dish,
                            onDish = onDish,
                            onRating = onRating,
                            userSettings = userSettings,
                            isOnMetered = isOnMetered,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            item(key = "footer") {
                footer(Modifier.animateItem())
            }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            if (dish.photoLink != null) {
                DishImageWithBadge(
                    dish = dish,
                    onRating = onRating,
                    priceType = userSettings.priceType,
                    downloadOnMetered = userSettings.downloadOnMetered,
                    isOnMetered = isOnMetered,
                )
            }

            DishNameRow(dish, Modifier.fillMaxWidth())
            if (dish.photoLink == null) {
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
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        DishImageRatio(
            photoLink = dish.photoLink ?: "Impossible",
            loadImmediately = downloadOnMetered || !isOnMetered,
            modifier =
            Modifier
                .align(Alignment.Center)
                .padding(Padding.Small),
        )
        DishBadgesColumn(
            dish = dish,
            onRating = onRating,
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}
