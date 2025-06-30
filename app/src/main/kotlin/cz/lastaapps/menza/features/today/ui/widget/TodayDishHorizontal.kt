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

import android.os.Build
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.features.today.ui.util.dishContainerKey
import cz.lastaapps.menza.features.today.ui.util.dishImageKey
import cz.lastaapps.menza.features.today.ui.util.dishTitleKey
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.AnimationScopes
import cz.lastaapps.menza.ui.util.appCardColors
import cz.lastaapps.menza.ui.util.sharedBounds
import cz.lastaapps.menza.ui.util.sharedContainer
import cz.lastaapps.menza.ui.util.sharedElement
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TodayDishHorizontal(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDish: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    onOliverRow: (Boolean) -> Unit,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    scroll: LazyListState,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxWidth(),
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
                userSettings = userSettings,
                isOnMetered = isOnMetered,
                onOliverRow = onOliverRow,
                scroll = scroll,
                header = header,
                footer = footer,
                scopes = scopes,
                modifier = Modifier.fillMaxSize(),
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
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    onOliverRow: (Boolean) -> Unit,
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
        state = scroll,
    ) {
        item(key = "header") {
            header(
                Modifier,
//                    Modifier.animateItem(),
            )
        }

        data.forEach { category ->
            item(key = category.name + "_cat_header") {
                DishHeader(
                    courseType = category,
                    modifier =
                        Modifier
                            .padding(bottom = Padding.Smaller),
//                                .animateItem(),
                )
            }
            item(key = category.name + "_content") {
                val isOnlyItem = category.dishList.size == 1

                if (isOnlyItem) {
                    DishItem(
                        dish = category.dishList.first(),
                        onDish = onDish,
                        userSettings = userSettings,
                        isOnMetered = isOnMetered,
                        scopes = scopes,
                        modifier =
                            Modifier
                                .fillMaxWidth(),
//                                    .animateItem(),
                    )
                } else {
                    @Composable
                    fun DishItemWrapper(
                        dish: Dish,
                        modifier: Modifier = Modifier,
                    ) {
                        DishItem(
                            dish = dish,
                            onDish = onDish,
                            userSettings = userSettings,
                            isOnMetered = isOnMetered,
                            scopes = scopes,
                            modifier = @Suppress("ktlint:compose:modifier-not-used-at-root")
                            modifier.sizeIn(maxWidth = 256.dp),
                        )
                    }

                    val horizontalArrangement =
                        Arrangement.spacedBy(
                            Padding.MidLarge,
                            Alignment.CenterHorizontally,
                        )
                    if (userSettings.useOliverRow) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = horizontalArrangement,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .animateContentSize(),
//                                        .animateItem(),
                        ) {
                            category.dishList.forEach { dish ->
                                DishItemWrapper(dish, Modifier)
                            }
                        }
                    } else {
                        LazyRow(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = horizontalArrangement,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(),
//                                        .animateItem(),
                        ) {
                            items(
                                category.dishList,
                                key = { "" + category.name + it.name },
                            ) { dish ->
                                DishItemWrapper(dish, Modifier.animateItem())
                            }
                        }
                    }
                }
            }
            item(key = category.name + "_spacer") {
                Spacer(Modifier.height(Padding.Small))
            }
        }

        item(key = "oliver") {
            OliverRowSwitch(
                useOliverRow = userSettings.useOliverRow,
                onOliverRow = onOliverRow,
                modifier =
                    Modifier
                        .fillMaxWidth(),
//                            .animateItem(),
            )
        }

        item(key = "footer") {
            footer(
                Modifier,
//                    Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDish: (Dish) -> Unit,
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
            modifier
                .sharedContainer(
                    scopes,
                    dishContainerKey(dish.id),
                    resizeMode = ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransitionShape = MaterialTheme.shapes.large,
                ),
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            DishImageWithBadge(
                dish = dish,
                priceType = userSettings.priceType,
                downloadOnMetered = userSettings.downloadOnMetered,
                isOnMetered = isOnMetered,
                modifier = Modifier.sharedElement(scopes, key = dishImageKey(dish.id)),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                DishNameRow(
                    dish = dish,
                    modifier = Modifier.sharedBounds(scopes, dishTitleKey(dish.id)),
                )

                DishInfoRow(dish)
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
        DishImageOrSupplement(
            dish = dish,
            loadImmediately = loadImmediately(downloadOnMetered, isOnMetered),
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(Padding.Small),
        )

        DishBadgesColumn(
            dish = dish,
            onRating = {},
            priceType = priceType,
            modifier = Modifier.align(Alignment.BottomEnd),
        )
    }
}

@Composable
private fun OliverRowSwitch(
    useOliverRow: Boolean,
    onOliverRow: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = { onOliverRow(!useOliverRow) },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val text =
                    if (!amIOliver()) {
                        stringResource(id = R.string.today_list_stable_row)
                    } else {
                        "Oliver jméno mé. 😎"
                    }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f),
                )
                Checkbox(checked = useOliverRow, onCheckedChange = onOliverRow)
            }
        }
    }
}

@Composable
private fun amIOliver() = remember { Build.MODEL == "CPH2305" }
