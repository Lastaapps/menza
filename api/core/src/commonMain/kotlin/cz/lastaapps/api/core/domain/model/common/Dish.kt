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

package cz.lastaapps.api.core.domain.model.common

import kotlinx.collections.immutable.ImmutableList

data class DishCategory(
    val nameShort: String?,
    val nameCs: String,
    val nameEn: String?,
    val dishList: ImmutableList<Dish>,
)

data class Dish(
    val amountCs: String?,
    val amountEn: String?,
    val nameEn: String?,
    val nameCs: String,
    val priceDiscounted: Float?,
    val priceNormal: Float?,
    // empty - no allergens
    // null  - unknown
    val allergens: ImmutableList<Int>?,
    val photoLink: String?,
    val pictogram: ImmutableList<String>,
    val servingPlaces: ImmutableList<ServingPlace>,
    val ingredients: ImmutableList<String>,
)

data class ServingPlace(
    val name: String,
    val abbrev: String,
)
