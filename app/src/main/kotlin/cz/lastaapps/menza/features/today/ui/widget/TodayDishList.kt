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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TodayDishList(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    header: @Composable () -> Unit,
    footer: @Composable () -> Unit,
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
                onDishSelected = onDishSelected,
                onNoItems = onNoItems,
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
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDishSelected: (Dish) -> Unit,
    onNoItems: () -> Unit,
    userSettings: TodayUserSettings,
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

    // showing items
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
        state = scroll,
    ) {
        item {
            header()
        }

        data.forEach { category ->
            stickyHeader {
                Surface(Modifier.fillMaxWidth()) {
                    DishHeader(
                        courseType = category,
                        language = userSettings.language,
                        modifier = Modifier.padding(bottom = Padding.Smaller),
                    )
                }
            }
            items(category.dishList) { dish ->
                DishItem(
                    dish = dish,
                    onDishSelected = onDishSelected,
                    userSettings = userSettings,
                    isOnMetered = isOnMetered,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            footer()
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.large,
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        Row(
            Modifier.padding(Padding.MidSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {

            DishImageWithBadge(
                dish = dish,
                priceType = userSettings.priceType,
                downloadOnMetered = userSettings.downloadOnMetered,
                imageScale = userSettings.imageScale,
                isOnMetered = isOnMetered,
            )
            Column(verticalArrangement = Arrangement.spacedBy(Padding.Small)) {
                DishNameRow(dish, userSettings.language)
                DishInfoRow(dish, userSettings.language)
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
                    top = Padding.Small,
                    bottom = Padding.Small,
                    end = Padding.Small,
                ),
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
