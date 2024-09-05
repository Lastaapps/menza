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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.Rating
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.ui.util.getPrice
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.persistentMapOf

@Composable
internal fun DishHeader(
    courseType: DishCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text =
            courseType.name
                ?: stringResource(id = R.string.today_list_category_other),
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
internal fun DishBadgesColumn(
    dish: Dish,
    onRating: (Dish) -> Unit,
    priceType: PriceType,
    modifier: Modifier = Modifier,
) = Column(
    modifier = modifier,
    horizontalAlignment = Alignment.End,
    verticalArrangement = Arrangement.spacedBy(Padding.Tiny),
) {
    DishPriceBadge(dish = dish, priceType = priceType)
    DishRatingBadge(rating = dish.rating, onRating = { onRating(dish) })
}

@Composable
internal fun DishBadgesRow(
    dish: Dish,
    onRating: (Dish) -> Unit,
    priceType: PriceType,
    modifier: Modifier = Modifier,
) = Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(Padding.Small, Alignment.End),
) {
    DishRatingBadge(rating = dish.rating, onRating = { onRating(dish) })
    DishPriceBadge(dish = dish, priceType = priceType)
}

@Composable
internal fun DishPriceBadge(
    dish: Dish,
    priceType: PriceType,
    modifier: Modifier = Modifier,
) {
    dish.getPrice(priceType)?.let { price ->
        Surface(
            modifier.shadow(4.dp),
            color = MaterialTheme.colorScheme.tertiary,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "$price Kč",
                style = MaterialTheme.typography.bodySmall,
                modifier =
                    Modifier.padding(
                        vertical = Padding.Tiny,
                        horizontal = Padding.Small,
                    ),
            )
        }
    }
}

@Composable
internal fun DishRatingBadge(
    rating: Rating,
    onRating: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier =
            modifier
                .shadow(4.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onRating() },
        color = MaterialTheme.colorScheme.inversePrimary,
    ) {
        val icons =
            remember {
                persistentMapOf(
                    "star" to
                        InlineTextContent(
                            Placeholder(1.1.em, 1.1.em, PlaceholderVerticalAlign.TextCenter),
                        ) {
                            Icon(Icons.Default.StarRate, contentDescription = null)
                        },
                    "person" to
                        InlineTextContent(
                            Placeholder(1.1.em, 1.1.em, PlaceholderVerticalAlign.TextCenter),
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                        },
                )
            }
        val text =
            buildAnnotatedString {
                if (rating.ratingCount != 0) {
                    append("%.1f".format(rating.overallRating))
                    appendInlineContent("star")
                    append(' ')
                }
                append(rating.ratingCount.toString())
                appendInlineContent("person")
            }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            inlineContent = icons,
            modifier =
                Modifier.padding(
                    vertical = Padding.Tiny,
                    horizontal = Padding.Small,
                ),
        )
    }
}

@Preview
@Composable
private fun DishRatingBadgePreview() =
    PreviewWrapper {
        DishRatingBadge(Rating("", 4.321f, 42, persistentMapOf()), {})
        DishRatingBadge(Rating("", 4.0f, 42, persistentMapOf()), {})
        DishRatingBadge(Rating("", 0.0f, 0, persistentMapOf()), {})
    }

@Composable
internal fun DishNameRow(
    dish: Dish,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dish.name,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
internal fun DishInfoRow(
    dish: Dish,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Padding.Smaller),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            dish.amount?.let { amount ->
                Text(text = amount)
            }
            dish.allergens?.let { allergens ->
                Text(
                    text = allergens.joinToString(separator = ", "),
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
