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

package cz.lastaapps.api.core.domain.model.rating

data class Rating(
    val overallRating: Float,
    val audience: UInt,
    val ratingCategories: RatingCategories,
) {
    companion object {
        val empty = Rating(0.0f, 0U, RatingCategories.empty)
    }

    object Mocked {
        val valid =
            Rating(
                overallRating = 4.321f,
                audience = 420U,
                ratingCategories =
                RatingCategories(
                    3.4f,
                    1.2f,
                    5.0f,
                ),
            )
    }
}
