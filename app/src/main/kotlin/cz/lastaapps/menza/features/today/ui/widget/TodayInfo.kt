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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.ServingPlace
import cz.lastaapps.api.core.domain.model.rating.Rating
import cz.lastaapps.api.core.domain.model.rating.RatingCategory
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.today.ui.util.allergenForId
import cz.lastaapps.menza.features.today.ui.util.formatPrice
import cz.lastaapps.menza.features.today.ui.util.toText
import cz.lastaapps.menza.ui.theme.MenzaColors
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentMapOf
import kotlin.math.max

@Composable
fun TodayInfo(
    dish: Dish,
    onRating: (Dish) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DishImageInfo(
            dish = dish,
        )

        Header(dish = dish)
        PriceView(dish = dish)
        IssueLocationList(
            list = dish.servingPlaces,
        )
        RatingOverview(rating = dish.rating, onRating = { onRating(dish) })
        AllergenList(
            allergens = dish.allergens,
        )
        Ingredients(
            ingredients = dish.ingredients,
        )
    }
}

@Composable
private fun Header(
    dish: Dish,
    modifier: Modifier = Modifier,
) {
    SelectionContainer {
        Text(
            text = dish.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = modifier,
        )
    }
}

@Composable
private fun PriceView(
    dish: Dish,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = dish.amount ?: "")
        val priceText =
            buildString {
                append(dish.priceDiscounted?.formatPrice() ?: "∅")
                append(" / ")
                append(dish.priceNormal?.formatPrice() ?: "∅")
                append(" Kč")
            }
        Text(
            text = priceText,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun IssueLocationList(
    list: ImmutableList<ServingPlace>,
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
            Text(text = it.name)
        }
    }
}

@Composable
private fun RatingOverview(
    rating: Rating,
    onRating: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable { onRating() },
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            val icons =
                remember {
                    persistentMapOf(
                        "star" to
                            InlineTextContent(
                                Placeholder(1.1.em, 1.1.em, PlaceholderVerticalAlign.TextTop),
                            ) {
                                Icon(Icons.Default.StarRate, contentDescription = null)
                            },
                        "person" to
                            InlineTextContent(
                                Placeholder(1.1.em, 1.1.em, PlaceholderVerticalAlign.TextCenter),
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null)
                            },
                        "big_star" to
                            InlineTextContent(
                                Placeholder(1.5.em, 1.5.em, PlaceholderVerticalAlign.Center),
                            ) {
                                Icon(
                                    Icons.Default.StarRate,
                                    contentDescription = null,
                                    tint = MenzaColors.gold,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            },
                    )
                }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Padding.Smaller),
            ) {
                Text(
                    text = stringResource(R.string.today_info_rating_title),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier =
                    Modifier
                        .weight(1f)
                        .align(Alignment.Top),
                )
                Column(
                    modifier = Modifier.align(Alignment.Bottom),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    if (rating.audience != 0U) {
                        Text(
                            text =
                                buildAnnotatedString {
                                    append("%.1f".format(rating.overallRating))
                                    appendInlineContent("big_star")
                                },
                            style = MaterialTheme.typography.titleLarge,
                            inlineContent = icons,
                        )
                    }
                    Text(
                        text =
                            buildAnnotatedString {
                                append(rating.audience.toString())
                                appendInlineContent("person")
                            },
                        style = MaterialTheme.typography.bodySmall,
                        inlineContent = icons,
                    )
                }
            }

            (RatingCategory.entries).forEach { category ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = category.toText(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        buildAnnotatedString {
                            append("%.1f".format(rating.ratingCategories[category]))
                            appendInlineContent("star")
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        inlineContent = icons,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RatingOverviewPreview() =
    PreviewWrapper {
        RatingOverview(Rating.Mocked.valid, onRating = {})
        RatingOverview(Rating.empty, onRating = {})
    }

@Composable
private fun AllergenList(
    allergens: ImmutableList<Int>?,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.today_info_allergens_title),
            style = MaterialTheme.typography.titleLarge,
        )

        when {
            allergens == null ->
                Text(stringResource(R.string.today_info_allergens_unknown))

            allergens.isEmpty() ->
                Text(stringResource(R.string.today_info_allergens_none))

            else ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    allergens.forEach {
                        AllergenRow(id = it)
                    }
                }
        }
    }
}

@Composable
private fun AllergenRow(
    id: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Padding.Smaller),
        modifier = modifier,
    ) {
        val info = allergenForId(id = id)

        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.Small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AllergenIdBadge(id = id)
            Text(
                text = info?.first ?: stringResource(R.string.today_info_unknown_allergen_title),
                style = MaterialTheme.typography.titleMedium,
            )
        }

        Text(
            text = info?.second ?: stringResource(R.string.today_info_unknown_allergen_description),
        )
    }
}

@Composable
private fun AllergenIdBadge(
    id: Int,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.tertiary,
        shape = CircleShape,
        modifier = modifier,
    ) {
        /*val density = LocalDensity.current
        val minSize = remember(density) {
            with(density) { 24.dp.roundToPx() }
        }*/
        Layout(
            content = {
                Text(
                    id.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier =
                        Modifier.padding(
                            start = 6.dp,
                            end = 6.dp,
                            top = 2.dp,
                            bottom = 2.dp,
                        ),
                )
            },
        ) { measurable, constrains ->
            val placeable = measurable[0].measure(constrains)
            // val h = max(placeable.height, minSize)
            val h = placeable.height
            val w = max(placeable.width, h)
            layout(w, h) {
                placeable.place(
                    (w - placeable.width) / 2,
                    (h - placeable.height) / 2,
                )
            }
        }
    }
}

@Composable
private fun Ingredients(
    ingredients: ImmutableList<String>,
    modifier: Modifier = Modifier,
) {
    if (ingredients.isEmpty()) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.Smaller),
    ) {
        Text(
            text = stringResource(R.string.today_info_ingredients_title),
            style = MaterialTheme.typography.titleLarge,
        )
        ingredients.forEach { ingredient ->
            Text(text = ingredient)
        }
    }
}

@Composable
private fun DishImageInfo(
    dish: Dish,
    modifier: Modifier = Modifier,
) {
    dish.photoLink?.let { photoLink ->
        DishImageRatio(
            photoLink = photoLink,
            loadImmediately = true,
            modifier = modifier,
        )
    } ?: run {
        Box(modifier.aspectRatio(4f / 1f))
    }
}
