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

package cz.lastaapps.api.core.domain.model

import kotlinx.collections.immutable.ImmutableList

data class DishCategory(
    val nameShort: String?,
    val name: String?,
    val dishList: ImmutableList<Dish>,
) {
    companion object {
        fun other(dishList: ImmutableList<Dish>) = DishCategory(null, null, dishList)
    }
}

data class Dish(
    val amount: String?,
    val name: String,
    val priceDiscounted: Float?,
    val priceNormal: Float?,
    // empty - no allergens
    // null  - unknown
    val allergens: ImmutableList<Int>?,
    val photoLink: String?,
    val pictogram: ImmutableList<String>,
    val servingPlaces: ImmutableList<ServingPlace>,
    val ingredients: ImmutableList<String>,
    val isActive: Boolean,
) {
    val rating: Rating = Rating.Mocked.valid
}

data class ServingPlace(
    val name: String,
    val abbrev: String,
)
