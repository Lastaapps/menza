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

package cz.lastaapps.menza.features.today.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringArrayResource
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishLanguage
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset
import cz.lastaapps.menza.features.settings.domain.model.isCzech

fun Dish.getPrice(type: PriceType) =
    when (type) {
        Discounted -> priceDiscounted ?: priceNormal
        Normal -> priceNormal
        Unset -> priceNormal
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

fun DishCategory.getName(language: DishLanguage): String? =
    nameEn.takeUnless { language.isCzech() } ?: nameCs

fun Dish.getName(language: DishLanguage): String =
    nameEn.takeUnless { language.isCzech() } ?: nameCs

fun Dish.getSecondaryName(language: DishLanguage): String? =
    nameCs.takeUnless { language.isCzech() } ?: nameEn

fun Dish.getAmount(language: DishLanguage): String? =
    amountEn.takeUnless { language.isCzech() } ?: amountCs

@Composable
fun allergenForId(id: Int): Pair<String, String>? {
    val labels = stringArrayResource(R.array.allergens_title)
    val descriptions = stringArrayResource(R.array.allergens_descriptions)
    if (id - 1 !in labels.indices) return null
    return labels[id - 1] to descriptions[id - 1]
}
