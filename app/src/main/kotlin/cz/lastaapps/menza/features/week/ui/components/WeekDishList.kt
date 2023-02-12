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

package cz.lastaapps.menza.features.week.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.api.core.domain.model.common.WeekDish
import cz.lastaapps.api.core.domain.model.common.WeekDishCategory
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.components.NoItems
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeekDishList(
    data: ImmutableList<WeekDayDish>,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    noItems: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = onRefresh,
    )
    Box(
        modifier = modifier
            .pullRefresh(pullState),
    ) {
        WeekDishContent(
            data = data,
            noItems = noItems,
            modifier = Modifier.fillMaxSize(),
        )

        MaterialPullIndicatorAligned(isLoading, pullState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WeekDishContent(
    data: ImmutableList<WeekDayDish>,
    noItems: () -> Unit,
    modifier: Modifier = Modifier,
) {
    //no data handling
    if (data.isEmpty()) {
        NoItems(modifier, noItems)
        return
    }

    // showing items
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        data.forEach { (date, categories) ->
            stickyHeader {
                //make header nontransparent
                Surface(Modifier.fillMaxWidth()) {
                    Box(Modifier.padding(bottom = 8.dp)) {
                        DayHeader(date = date)
                    }
                }
            }
            categories.forEach { category ->
                item { CourseHeader(courseType = category) }
//                item { Spacer(Modifier.height(4.dp)) }

                items(
                    category.dishList,
                    key = { "" + date + category.name + it.name }) { dish ->
                    WeekDishItem(dish = dish, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun DayHeader(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
    ) {
        Text(
            text = date.toJavaLocalDate().format(format),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(4.dp)
                .padding(start = 8.dp, end = 8.dp),
        )
    }
}

@Composable
private fun CourseHeader(
    courseType: WeekDishCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text = courseType.name,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
    )
}

@Composable
private fun WeekDishItem(
    dish: WeekDish,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(dish.amount ?: "", Modifier.width(48.dp))
            Text(dish.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}


