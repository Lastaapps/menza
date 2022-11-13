/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.today

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.CollectErrors
import cz.lastaapps.menza.ui.LocalConnectivityProvider
import cz.lastaapps.menza.ui.dests.panels.Panels
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.*
import cz.lastaapps.menza.ui.isMetered
import cz.lastaapps.menza.ui.layout.menza.MenzaNotSelected
import cz.lastaapps.menza.ui.root.locals.LocalSnackbarProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.delay

@Composable
fun TodayDishList(
    navController: NavController,
    menzaId: MenzaId?,
    onDishSelected: (Dish) -> Unit,
    viewModel: TodayViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
    scroll: LazyListState = rememberLazyListState(),
) {
    val priceType by settingsViewModel.sett.priceType.collectAsState()
    val downloadOnMetered by settingsViewModel.sett.imagesOnMetered.collectAsState()
    val imageSizeRation by settingsViewModel.sett.imageSize.collectAsState()

    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (menzaId == null) {
            MenzaNotSelected(navController, Modifier.fillMaxSize())
        } else {

            //show error snackBars
            val snackbarHostState = LocalSnackbarProvider.current
            CollectErrors(snackbarHostState, viewModel.errors)

            //getting locale for food sorting
            val config = LocalContext.current.resources.configuration
            val locale = remember(config) {
                @Suppress("DEPRECATION")
                if (Build.VERSION_CODES.N >= Build.VERSION.SDK_INT)
                    config.locale else config.locales[0]
            }

            //getting data
            val data by remember(menzaId) { viewModel.getData(menzaId, locale) }.collectAsState()

            val isRefreshing by remember(menzaId) {
                viewModel.isRefreshing(menzaId)
            }.collectAsState()

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refresh(menzaId, locale) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) {
                Crossfade(targetState = data) { currentData ->
                    Surface(shape = MaterialTheme.shapes.large) {
                        DishContent(
                            menzaId, currentData, onDishSelected,
                            priceType, downloadOnMetered, imageSizeRation, scroll,
                            Modifier
                                .padding(top = 4.dp) // so text is not cut off
                                .fillMaxSize(),
                        )
                    }
                }
            }
        }
        Panels(Modifier.fillMaxWidth())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DishContent(
    menzaId: MenzaId,
    data: ImmutableList<DishTypeList>,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    imageSizeRation: Float,
    scroll: LazyListState,
    modifier: Modifier = Modifier
) {

    //no data handling
    if (data.isEmpty()) {
        NoItems(modifier, menzaId)
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // showing items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = scroll,
        ) {
            data.forEach { dishType ->
                stickyHeader {
                    Surface(Modifier.fillMaxWidth()) {
                        DishHeader(courseType = dishType.first, Modifier.padding(bottom = 4.dp))
                    }
                }
                items(dishType.second) { dish ->
                    DishItem(
                        dish, onDishSelected,
                        priceType, downloadOnMetered, imageSizeRation,
                        Modifier.fillMaxWidth(),
                    )
                }
            }
            return@LazyColumn
        }
    }
}

@Composable
private fun NoItems(modifier: Modifier, menzaId: MenzaId) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        val uriHandler = LocalUriHandler.current
        Text(stringResource(R.string.today_list_none))

        // show web button after 3 seconds
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(3000)
            visible = true
        }
        AnimatedVisibility(visible) {
            TextButton(onClick = { uriHandler.openUri("https://agata.suz.cvut.cz/jidelnicky/index.php?clPodsystem=${menzaId.id}") }) {
                Text(stringResource(R.string.today_list_web))
            }
        }
    }
}

@Composable
private fun DishHeader(courseType: CourseType, modifier: Modifier = Modifier) {
    Text(text = courseType.type, modifier = modifier, style = MaterialTheme.typography.titleMedium)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    imageSizeRation: Float,
    modifier: Modifier = Modifier
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

            DishImageWithBadge(dish, priceType, downloadOnMetered, imageSizeRation)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DishNameRow(dish)
                DishInfoRow(dish)
            }
        }
    }
}

@Composable
private fun DishNameRow(dish: Dish, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
        modifier = modifier,
    ) {
        Text(dish.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)

        // TODO resolve dish issue windows
        // Hidden to a normal user until it's verified
        // Still available in dish details
        if (BuildConfig.DEBUG) {
            Column(
                Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dish.issuePlaces.forEach {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "${it.abbrev} ${it.windowsId}",
                            textAlign = TextAlign.End,
                            modifier = Modifier.padding(2.dp),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DishInfoRow(dish: Dish, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(dish.amount?.amount ?: "")
        Text(
            dish.allergens.map { it.id }.joinToString(separator = ","),
            Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun DishImageWithBadge(
    dish: Dish,
    priceType: PriceType,
    downloadOnMetered: Boolean,
    imageSizeRation: Float,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        DishImage(
            dish = dish, downloadOnMetered, imageSizeRation,
            Modifier
                .align(Alignment.Center)
                .padding(top = 8.dp, bottom = 8.dp, end = 8.dp)
        )
        DishBadge(dish = dish, priceType, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
private fun DishBadge(dish: Dish, priceType: PriceType, modifier: Modifier = Modifier) {
    dish.getPrice(priceType)?.let { price ->
        Surface(
            modifier,
            color = MaterialTheme.colorScheme.tertiary,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "${price.price} Kč",
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
    imageSizeRation: Float,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        val size = (96 * imageSizeRation).dp
        val imageModifier = Modifier.size(size)


        if (dish.imageUrl != null) {

            var retryHash by remember { mutableStateOf(0) }
            val isMetered = LocalConnectivityProvider.current
            var userAllowed by rememberSaveable(dish.imageUrl) { mutableStateOf(false) }
            val canDownload = remember(isMetered, userAllowed) {
                downloadOnMetered || !isMetered.isMetered() || userAllowed
            }

            val imageRequest = with(ImageRequest.Builder(LocalContext.current)) {
                diskCacheKey(dish.imageUrl)
                memoryCacheKey(dish.imageUrl)
                crossfade(true)
                setParameter("retry_hash", retryHash)
                // if user is not on a metered network, images are going to be loaded from cache
                if (canDownload)
                    data(dish.imageUrl)
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
                    imageRequest, dish.name,
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
                    contentDescription = dish.name,
                )
            }
        }
    }
}
