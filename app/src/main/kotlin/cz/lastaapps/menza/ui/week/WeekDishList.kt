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

package cz.lastaapps.menza.ui.week

import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.menza.ui.LocalSnackbarProvider
import kotlinx.coroutines.channels.consumeEach
import kotlinx.datetime.LocalDate

@Composable
fun WeekDishList(
    menzaId: MenzaId?,
    modifier: Modifier = Modifier,
    viewModel: WeekViewModel,
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
            WeekDishContent(data = data, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun WeekDishContent(
    data: List<DayDishList>,
    modifier: Modifier = Modifier,
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
        items(data) { dayDishList ->
            DayHeader(dayDishList.date)

            Column {
                dayDishList.dishes.forEach { courseAndDish ->

                    CourseHeader(courseType = courseAndDish.first)
                    Column {
                        courseAndDish.second.forEach { dish ->
                            WeekDishItem(dish = dish)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayHeader(date: LocalDate, modifier: Modifier = Modifier) {
    Text(text = date.toString(), modifier = modifier)
}

@Composable
private fun CourseHeader(courseType: CourseType, modifier: Modifier = Modifier) {
    Text(text = courseType.type, modifier = modifier)
}

@Composable
private fun WeekDishItem(dish: WeekDish, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = modifier,
    ) {
        Text(dish.name, Modifier.padding(8.dp))
    }
}


