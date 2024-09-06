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

import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

data class Rating(
    val dishID: String,
    val overallRating: Float,
    val ratingCount: Int,
    val ratingCategories: ImmutableMap<RatingCategory, Float>,
) {
    object Mocked {
        val valid =
            Rating(
                dishID = "",
                overallRating = 4.321f,
                ratingCount = 420,
                ratingCategories =
                    persistentMapOf(
                        RatingCategory.TASTE to 3.4f,
                        RatingCategory.WORTHINESS to 1.2f,
                        RatingCategory.PORTION_SIZE to 5.0f,
                    ),
            )
        val noRatings =
            Rating(
                dishID = "",
                overallRating = 0.0f,
                ratingCount = 0,
                ratingCategories =
                    persistentMapOf(
                        RatingCategory.TASTE to 0.0f,
                        RatingCategory.WORTHINESS to 0.0f,
                        RatingCategory.PORTION_SIZE to 0.0f,
                    ),
            )
    }
}

enum class RatingCategory {
    TASTE,
    PORTION_SIZE,
    WORTHINESS,
}
