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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.ServingPlace
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.today.ui.util.allergenForId
import cz.lastaapps.menza.features.today.ui.util.formatPrice
import cz.lastaapps.menza.features.today.ui.util.getAmount
import cz.lastaapps.menza.features.today.ui.util.getName
import cz.lastaapps.menza.features.today.ui.util.getSecondaryName
import cz.lastaapps.menza.ui.theme.Padding
import kotlin.math.max
import kotlinx.collections.immutable.ImmutableList

@Composable
fun TodayInfo(
    dish: Dish,
    language: DishLanguage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DishImageInfo(
            dish = dish,
        )

        Header(
            dish = dish,
            language = language,
        )
        PriceView(
            dish = dish,
            language = language,
        )
        IssueLocationList(
            list = dish.servingPlaces,
        )
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
    language: DishLanguage,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SelectionContainer {
            Text(
                text = dish.getName(language),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier,
            )
        }

        dish.getSecondaryName(language)?.let { name ->
            SelectionContainer {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}

@Composable
private fun PriceView(
    dish: Dish,
    language: DishLanguage,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        Text(text = dish.getAmount(language) ?: "")
        val priceText = buildString {
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
private fun AllergenRow(id: Int, modifier: Modifier = Modifier) {
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
private fun AllergenIdBadge(id: Int, modifier: Modifier = Modifier) {
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
                    modifier = Modifier.padding(
                        start = 6.dp, end = 6.dp,
                        top = 2.dp, bottom = 2.dp,
                    ),
                )
            },
        ) { measurable, constrains ->
            val placeable = measurable[0].measure(constrains)
            //val h = max(placeable.height, minSize)
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
private fun DishImageInfo(dish: Dish, modifier: Modifier = Modifier) {
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
