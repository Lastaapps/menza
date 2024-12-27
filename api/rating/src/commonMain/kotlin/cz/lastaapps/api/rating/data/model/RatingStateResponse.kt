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

package cz.lastaapps.api.rating.data.model

import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.Rating
import cz.lastaapps.api.core.domain.model.rating.RatingCategories
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.serialization.Serializable

@Serializable
internal data class RatingStateResponse(
    val id: String,
    val nameCs: String? = null,
    val nameEn: String? = null,
    val combined: Category,
    val taste: Category? = null,
    val portion: Category? = null,
    val worthiness: Category? = null,
) {
    @Serializable
    data class Category(
        val audience: UInt,
        val average: Float,
    )
}

internal fun List<RatingStateResponse>.toDomain(): ImmutableMap<DishID, Rating> =
    associate {
        DishID(it.id) to it.toDomain()
    }.toImmutableMap()

internal fun RatingStateResponse.toDomain(): Rating =
    Rating(
        overallRating = combined.average,
        audience = combined.audience,
        ratingCategories =
        RatingCategories(
            taste = taste?.average,
            portionSize = portion?.average,
            worthiness = worthiness?.average,
        ),
    )
