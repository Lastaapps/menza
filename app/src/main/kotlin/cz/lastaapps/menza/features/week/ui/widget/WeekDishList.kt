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

package cz.lastaapps.menza.features.week.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.WeekDishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.ui.util.getPrice
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeekDishList(
    data: ImmutableList<WeekDayDish>,
    priceType: PriceType,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    noItems: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        WeekDishContent(
            data = data,
            priceType = priceType,
            noItems = noItems,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("ktlint:compose:modifier-reused-check")
private fun WeekDishContent(
    data: ImmutableList<WeekDayDish>,
    priceType: PriceType,
    noItems: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // no data handling
    if (data.isEmpty()) {
        NoItems(modifier, noItems)
        return
    }

    val measurement = rememberTextMeasurer()
    val longestAmount = remember(data) { data.longestAmountOrPrice() }
    val amountWidthPx =
        remember(longestAmount, measurement) {
            measurement
                .measure(
                    buildString(longestAmount) { repeat(longestAmount) { append('m') } },
                ).size.width * .8f
        }
    val amountWidth = with(LocalDensity.current) { amountWidthPx.toDp() }

    // showing items
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        data.forEach { (date, categories) ->
            stickyHeader {
                // make header nontransparent
                Surface(Modifier.fillMaxWidth()) {
                    Box(Modifier.padding(bottom = Padding.Small)) {
                        DayHeader(date = date)
                    }
                }
            }
            categories.forEach { category ->
                item(
                    key = "" + date + category.name,
                ) {
                    CourseHeader(
                        courseType = category,
                        modifier = Modifier.animateItem(),
                    )
                }

                items(
                    category.dishList,
                    key = { "" + date + category.name + it.name },
                ) { dish ->
                    WeekDishItem(
                        dish = dish,
                        priceType = priceType,
                        amountWidth = amountWidth,
                        modifier =
                            Modifier
                                .animateItem()
                                .fillMaxWidth(),
                    )
                }

                item { Spacer(Modifier.height(Padding.Tiny)) }
            }

            item { Spacer(Modifier.height(Padding.MidSmall)) }
        }
    }
}

private fun ImmutableList<WeekDayDish>.longestAmountOrPrice(): Int =
    maxOf {
        it.categories.maxOf { category ->
            category.dishList.maxOf { dish ->
                kotlin.math.max(
                    dish.amount?.length ?: 0,
                    (dish.priceNormal ?: dish.priceDiscounted)
                        ?.let { price -> kotlin.math.log10(price) }
                        ?.plus(3)
                        ?.roundToInt() ?: 0,
                )
            }
        }
    }

@Composable
private fun rememberDateFormatter(): @Composable (LocalDate) -> String {
    val locale = androidx.compose.ui.text.intl.Locale.current
    return remember(locale) {
        val javaLocale = Locale(locale.language, locale.region)
        val dayOfWeekHeaderFormat = DateTimeFormatter.ofPattern("EEEE", javaLocale)
        val dateHeaderFormat =
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).withLocale(javaLocale)

        val lambda: @Composable (LocalDate) -> String = { date: LocalDate ->
            remember(date) {
                val javaDate = date.toJavaLocalDate()
                dayOfWeekHeaderFormat.format(javaDate) + " " + dateHeaderFormat.format(javaDate)
            }
        }
        lambda
    }
}

@Composable
private fun DayHeader(
    date: LocalDate,
    modifier: Modifier = Modifier,
) {
    val formatter = rememberDateFormatter()

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
    ) {
        Text(
            text = formatter(date),
            style = MaterialTheme.typography.titleLarge,
            modifier =
                Modifier
                    .padding(
                        horizontal = Padding.MidLarge,
                        vertical = Padding.Smaller,
                    ),
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
    amountWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Row(
            Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.sizeIn(minWidth = amountWidth),
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                dish.amount?.let { Text(it) }
                dish.getPrice(priceType)?.let { Text("$it Kč") }
            }

            SelectionContainer {
                Text(dish.name, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
