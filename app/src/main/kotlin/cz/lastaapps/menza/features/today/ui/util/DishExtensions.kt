/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.today.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.rating.RatingCategory
import cz.lastaapps.api.core.domain.model.rating.RatingCategory.PORTION_SIZE
import cz.lastaapps.api.core.domain.model.rating.RatingCategory.TASTE
import cz.lastaapps.api.core.domain.model.rating.RatingCategory.WORTHINESS
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.Currency
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset

fun Dish.getPrice(
    type: PriceType,
    currency: Currency = Currency.CZK,
) = when (type) {
    Discounted -> priceDiscounted ?: priceNormal
    Normal -> priceNormal
    Unset -> priceNormal
}?.let {
    when (currency) {
        Currency.NONE -> null
        Currency.CZK -> it
        // based on Strahov Bar 10 - check for price update once a week at least
        Currency.BEER -> it / 45f
        // TODO update me every year
        // based on Czech National Bank: https://www.kurzy.cz/kurzy-men/jednotny-kurz/
        Currency.EUR -> it / 25.160f
        Currency.USD -> it / 23.280f
    }
}?.formatPrice()

fun WeekDish.getPrice(type: PriceType) =
    when (type) {
        Discounted -> priceDiscounted ?: priceNormal
        Normal -> priceNormal
        Unset -> priceNormal
    }?.formatPrice()

fun Float.formatPrice() =
    if (this.mod(1f) == 0f) {
        "%.0f".format(this)
    } else {
        "%.2f".format(this)
    }

@Composable
fun allergenForId(id: Int): Pair<String, String>? {
    val labels = stringArrayResource(R.array.allergens_title)
    val descriptions = stringArrayResource(R.array.allergens_descriptions)
    if (id - 1 !in labels.indices) return null
    return labels[id - 1] to descriptions[id - 1]
}

@Composable
fun RatingCategory.toText() =
    when (this) {
        TASTE -> R.string.rating_category_taste
        PORTION_SIZE -> R.string.rating_category_portion_size
        WORTHINESS -> R.string.rating_category_worthiness
    }.let { stringResource(it) }
