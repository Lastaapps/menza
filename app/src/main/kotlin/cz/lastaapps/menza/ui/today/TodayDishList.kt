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

package cz.lastaapps.menza.ui.today

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.AsyncImageContent
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalSnackbarProvider
import cz.lastaapps.menza.ui.menza.MenzaNotSelected
import cz.lastaapps.menza.ui.settings.SettingsViewModel
import cz.lastaapps.menza.ui.settings.store.PriceType
import cz.lastaapps.menza.ui.settings.store.getPrice
import cz.lastaapps.menza.ui.settings.store.priceType
import kotlinx.coroutines.channels.consumeEach

@Composable
fun TodayDishList(
    navController: NavController,
    menzaId: MenzaId?,
    onDishSelected: (Dish) -> Unit,
    viewModel: TodayViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val priceType by settingsViewModel.sett.priceType.collectAsState()
    val onPriceType = { type: PriceType -> settingsViewModel.setPriceType(type) }

    if (menzaId == null) {
        MenzaNotSelected(navController, modifier)
    } else {

        //show error snackBars
        val snackbarHostState = LocalSnackbarProvider.current
        LaunchedEffect("") {
            viewModel.errors.consumeEach {
                snackbarHostState.showSnackbar(it.toString())
            }
        }

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
            modifier = modifier,
        ) {
            DishContent(data = data, onDishSelected, priceType, onPriceType, Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DishContent(
    data: List<DishTypeList>,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    onPriceType: (PriceType) -> Unit,
    modifier: Modifier = Modifier
) {

    //no data handling
    if (data.isEmpty()) {
        Box(
            modifier = modifier.verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            Text("No data available")
        }
        return
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // showing items
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            data.forEach { dishType ->
                stickyHeader {
                    Surface(Modifier.fillMaxWidth()) {
                        DishHeader(courseType = dishType.first, Modifier.padding(bottom = 8.dp))
                    }
                }
                items(dishType.second) { dish ->
                    DishItem(
                        dish = dish,
                        onDishSelected = onDishSelected,
                        priceType,
                        Modifier.fillMaxWidth()
                    )
                }
            }
            return@LazyColumn
        }
        PriceTypeUnspecified(priceType, onPriceType, Modifier.fillMaxWidth())
    }
}

@Composable
private fun PriceTypeUnspecified(
    priceType: PriceType,
    onPriceType: (PriceType) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (priceType == PriceType.Unset) {
        Surface(color = MaterialTheme.colorScheme.tertiaryContainer, modifier = modifier) {
            Column(
                Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    "Which prices do you want to see?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleLarge,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(IntrinsicSize.Max)
                ) {
                    Button(
                        onClick = { onPriceType(PriceType.Discounted) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text("Discounted", textAlign = TextAlign.Center)
                    }
                    Button(
                        onClick = { onPriceType(PriceType.Normal) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text("Normal full", textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun DishHeader(courseType: CourseType, modifier: Modifier = Modifier) {
    Text(text = courseType.type, modifier = modifier, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    priceType: PriceType,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            DishImageWithBadge(dish = dish, priceType)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DishNameRow(dish = dish)
                DishInfoRow(dish = dish)
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
        Text(dish.name, Modifier.weight(1f))
        Column(
            Modifier.width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dish.issuePlaces.forEach {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
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
private fun DishImageWithBadge(dish: Dish, priceType: PriceType, modifier: Modifier = Modifier) {
    ConstraintLayout(modifier) {
        val (imgConst, priceConst) = createRefs()

        DishImage(dish = dish, Modifier.constrainAs(imgConst) {
            centerHorizontallyTo(parent)
            centerVerticallyTo(parent)
        })

        DishBadge(dish = dish, priceType, Modifier.constrainAs(priceConst) {
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        })
    }
}

@Composable
private fun DishBadge(dish: Dish, priceType: PriceType, modifier: Modifier = Modifier) {
    Surface(modifier, color = MaterialTheme.colorScheme.tertiary) {
        Text(
            text = "${dish.getPrice(priceType).price} Kč",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(2.dp)
        )
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DishImage(dish: Dish, modifier: Modifier = Modifier) {
    Box(modifier) {
        val size = 64.dp
        val imageModifier = Modifier
            .size(size)
            .padding(bottom = 8.dp, end = 8.dp)

        if (dish.imageUrl != null) {

            var retryHash by remember { mutableStateOf(0) }

            val imageRequest = ImageRequest.Builder(LocalContext.current)
                .data(dish.imageUrl)
                .crossfade(true)
                .setParameter("retry_hash", retryHash)
                .build()

            AsyncImage(
                imageRequest, dish.name,
                loading = {
                    Box(
                        imageModifier
                            .placeholder(
                                true, color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                                highlight = PlaceholderHighlight.fade(
                                    highlightColor = MaterialTheme.colorScheme.secondary,
                                )
                            )
                            .clickable { retryHash++ }
                    )
                },
                error = {
                    Box(
                        imageModifier.clickable { retryHash++ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            "Failed to load an image",
                        )
                    }
                },
                success = {
                    AsyncImageContent(
                        contentScale = ContentScale.Crop,
                        modifier = imageModifier,
                    )
                },
            )

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