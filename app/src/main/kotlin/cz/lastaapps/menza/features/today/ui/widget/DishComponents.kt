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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.today.ui.util.getPrice
import cz.lastaapps.menza.ui.theme.Padding


@Composable
internal fun DishHeader(
    courseType: DishCategory,
    modifier: Modifier = Modifier,
) {
    Text(
        text = courseType.name
            ?: stringResource(id = R.string.today_list_category_other),
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
internal fun DishBadge(
    dish: Dish,
    priceType: PriceType,
    modifier: Modifier = Modifier,
) {
    dish.getPrice(priceType)?.let { price ->
        Surface(
            modifier,
            color = MaterialTheme.colorScheme.tertiary,
            shape = MaterialTheme.shapes.medium,
        ) {
            Text(
                text = "$price Kč",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    vertical = Padding.Tiny,
                    horizontal = Padding.Small,
                ),
            )
        }
    }
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
