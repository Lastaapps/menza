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
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalSnackbarProvider
import kotlinx.coroutines.channels.consumeEach

@Composable
fun TodayDishList(
    menzaId: MenzaId?,
    onDishSelected: (Dish) -> Unit,
    viewModel: TodayViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No menza selected")
        }
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
            DishContent(data = data, onDishSelected, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun DishContent(
    data: List<DishTypeList>,
    onDishSelected: (Dish) -> Unit,
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

    // showing items
    LazyColumn(modifier = modifier) {
        items(data) { dishType ->
            DishHeader(courseType = dishType.first)
            dishType.second.forEach { dish ->
                DishItem(dish = dish, onDishSelected = onDishSelected)
            }
        }
    }
}

@Composable
private fun DishHeader(courseType: CourseType, modifier: Modifier = Modifier) {
    Text(text = courseType.type, modifier = modifier)
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DishItem(dish: Dish, onDishSelected: (Dish) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        Row(Modifier.padding(8.dp)) {
            val size = 64.dp
            val sizePx = with(LocalDensity.current) { size.roundToPx() }

            if (dish.imageUrl != null) {
                val imagePainter = rememberImagePainter(dish.imageUrl) {
                    //transformations(CircleCropTransformation())
                    size(sizePx)
                }

                val painter: Any = when (imagePainter.state) {
                    ImagePainter.State.Empty -> Icons.Default.Downloading
                    is ImagePainter.State.Loading -> Icons.Default.Downloading
                    is ImagePainter.State.Success -> imagePainter.state.painter!!
                    is ImagePainter.State.Error -> Icons.Default.Error
                }

                if (painter is Painter)
                    Image(painter, contentDescription = dish.name, Modifier.size(size))
                else if (painter is ImageVector)
                    Image(painter, contentDescription = dish.name, Modifier.size(size))
            } else {
                Image(
                    Icons.Default.Restaurant,
                    contentDescription = dish.name,
                    Modifier.size(size)
                )
            }
            Text(dish.name, Modifier.padding(8.dp))
        }
    }
}

