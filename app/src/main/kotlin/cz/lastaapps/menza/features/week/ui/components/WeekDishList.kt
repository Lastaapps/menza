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
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.WeekDishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.ui.util.getPrice
import cz.lastaapps.menza.ui.components.MaterialPullIndicatorAligned
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.theme.MenzaPadding
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeekDishList(
    data: ImmutableList<WeekDayDish>,
    priceType: PriceType,
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
            priceType = priceType,
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
    priceType: PriceType,
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

                items(
                    category.dishList,
                    key = { "" + date + category.name + it.name }) { dish ->
                    WeekDishItem(
                        dish = dish,
                        priceType = priceType,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item { Spacer(Modifier.height(MenzaPadding.Tiny)) }
            }

            item { Spacer(Modifier.height(MenzaPadding.MidSmall)) }
        }
    }
}

private val dateHeaderFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
@Composable
private fun DayHeader(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
    ) {
        Text(
            text = date.toJavaLocalDate().format(dateHeaderFormat),
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
    priceType: PriceType,
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
            Column(
                modifier = Modifier.sizeIn(minWidth = 60.dp),
                verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
            ) {
                dish.amount?.let { Text(it) }
                dish.getPrice(priceType)?.let { Text("$it Kč") }
            }
            Text(dish.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}


