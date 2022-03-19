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

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.day.IssueLocation
import cz.lastaapps.menza.R

@Composable
fun NoDishSelected(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.today_no_dish))
    }
}

@Composable
fun TodayInfo(
    dish: Dish,
    todayViewModel: TodayViewModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DishImage(dish = dish, Modifier.fillMaxWidth())

        Header(dish = dish)
        PriceView(dish = dish, Modifier.fillMaxWidth())
        IssueLocationList(list = dish.issuePlaces)
        AllergenList(dish = dish, todayViewModel = todayViewModel)
    }
}

@Composable
private fun Header(dish: Dish, modifier: Modifier = Modifier) {
    Text(
        text = dish.name,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}

@Composable
private fun PriceView(dish: Dish, modifier: Modifier = Modifier) {
    Row(modifier) {
        Text(text = dish.amount?.amount ?: "")
        Text(
            text = "${dish.priceStudent.price} / ${dish.priceNormal.price} Kč",
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IssueLocationList(
    list: List<IssueLocation>,
    modifier: Modifier = Modifier,
) {
    if (list.isEmpty()) return
    Column(modifier) {
        Row {
            Text(
                text = stringResource(R.string.today_info_location),
                style = MaterialTheme.typography.titleMedium,
            )
            /*Text(
                text = stringResource(R.string.today_info_window),
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
            )*/
        }
        list.forEach {
            Row {
                Text(text = it.name)
                Text(
                    text = it.windowsId.toString(),
                    textAlign = TextAlign.End,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun AllergenList(
    dish: Dish,
    todayViewModel: TodayViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val data by todayViewModel.getAllergenForIds(dish.allergens).collectAsState(emptyList())

        Text(
            stringResource(R.string.today_info_allergens_title),
            style = MaterialTheme.typography.titleLarge
        )

        if (data.isEmpty()) {
            Text(stringResource(R.string.today_info_allergens_none))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                data.forEach {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape,
                            ) {
                                Box(
                                    modifier = Modifier.size(24.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        "${it.id.id}",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(2.dp),
                                    )
                                }
                            }
                            Text(it.name, style = MaterialTheme.typography.titleMedium)
                        }
                        Text(it.description)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DishImage(dish: Dish, modifier: Modifier = Modifier) {
    Box(modifier.animateContentSize()) {

        if (dish.imageUrl != null) {

            //temporary solution for refreshing
            var retryHash by remember { mutableStateOf(0) }

            val imageRequest = with(ImageRequest.Builder(LocalContext.current)) {
                data(dish.imageUrl)
                diskCacheKey(dish.imageUrl)
                memoryCacheKey(dish.imageUrl)
                crossfade(true)
                setParameter("retry_hash", retryHash)
            }.build()

            SubcomposeAsyncImage(
                imageRequest, dish.name,
                loading = {
                    Box(
                        Modifier
                            .aspectRatio(4f / 3f)
                            .placeholder(
                                true, color = MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(4.dp),
                                highlight = PlaceholderHighlight.fade(
                                    highlightColor = MaterialTheme.colorScheme.primary,
                                )
                            )
                            .clickable { retryHash++ }
                    )
                },
                error = {
                    Box(
                        Modifier
                            .aspectRatio(4f / 3f)
                            .clickable { retryHash++ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            stringResource(R.string.today_info_image_load_failed)
                        )
                    }
                },
                success = {
                    SubcomposeAsyncImageContent(
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        } else {
            Box(Modifier.aspectRatio(4f / 1f))
        }
    }
}
