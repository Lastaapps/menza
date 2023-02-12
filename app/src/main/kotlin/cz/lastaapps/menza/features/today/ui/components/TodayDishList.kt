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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech
import cz.lastaapps.menza.features.today.ui.util.getAmount
import cz.lastaapps.menza.features.today.ui.util.getName
import cz.lastaapps.menza.features.today.ui.util.getPrice
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.dests.panels.Panels
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

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
    modifier: Modifier = Modifier,
    scroll: LazyListState = rememberLazyListState(),
    pullState: PullRefreshState = rememberPullRefreshState(
        refreshing = isLoading, onRefresh = onRefresh,
    ),
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                    modifier = Modifier
                        .padding(top = MenzaPadding.Smaller) // so text is not cut off
                        .fillMaxSize(),
                )
            }

            MaterialPullIndicatorAligned(isLoading, pullState)
        }

        // TODO move
        Panels(Modifier.fillMaxWidth())
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
        }
    }
}

@Composable
private fun NoItems(modifier: Modifier, onNoItems: () -> Unit) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Text(stringResource(R.string.today_list_none))

        // show web button after 3 seconds
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(3000)
            visible = true
        }
        AnimatedVisibility(visible) {
            TextButton(onClick = onNoItems) {
                Text(stringResource(R.string.today_list_web))
            }
        }
    }
}

@Composable
private fun DishHeader(
    courseType: DishCategory,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Text(
        text = courseType.getName(showCzech),
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
    )
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
private fun DishNameRow(
    dish: Dish,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dish.getName(showCzech),
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
private fun DishInfoRow(
    dish: Dish,
    showCzech: ShowCzech,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Smaller),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
        ) {
            dish.getAmount(showCzech)?.let { amount ->
                Text(text = amount)
            }
            dish.allergens?.let { allergens ->
                Text(
                    text = allergens.joinToString(separator = ","),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                )
            }
        }
        dish.ingredients.takeIf { it.isNotEmpty() }?.let { ingredients ->
            Text(
                text = ingredients.joinToString(separator = ", "),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodySmall,
            )
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
        DishImage(
            dish = dish,
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
private fun DishBadge(
    dish: Dish,
    priceType: PriceType,
    modifier: Modifier = Modifier,
) {
    dish.getPrice(priceType)?.let { price ->
        Surface(
            modifier,
            color = MaterialTheme.colorScheme.tertiary,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "$price Kč",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
private fun DishImage(
    dish: Dish,
    downloadOnMetered: Boolean,
    imageScale: Float,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        val size = (96 * imageScale).dp
        val imageModifier = Modifier.size(size)


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
                with(LocalDensity.current) { size(size.roundToPx()) }
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
