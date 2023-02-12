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

package cz.lastaapps.menza.features.today.ui.util

import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech

fun Dish.getPrice(type: PriceType) =
    when (type) {
        Discounted -> priceDiscount ?: priceNormal
        Normal -> priceNormal
        Unset -> priceNormal
    }

fun DishCategory.getName(showCzech: ShowCzech): String =
    nameEn.takeUnless { showCzech.czech } ?: nameCs

fun Dish.getName(showCzech: ShowCzech): String =
    nameEn.takeUnless { showCzech.czech } ?: nameCs

fun Dish.getAmount(showCzech: ShowCzech): String? =
    amountEn.takeUnless { showCzech.czech } ?: amountCs
