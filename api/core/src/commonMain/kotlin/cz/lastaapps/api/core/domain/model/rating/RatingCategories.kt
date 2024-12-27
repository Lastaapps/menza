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

import cz.lastaapps.api.core.domain.model.rating.RatingCategory.PORTION_SIZE
import cz.lastaapps.api.core.domain.model.rating.RatingCategory.TASTE
import cz.lastaapps.api.core.domain.model.rating.RatingCategory.WORTHINESS

data class RatingCategories(
    private val taste: Float?,
    private val portionSize: Float?,
    private val worthiness: Float?,
) {
    operator fun get(category: RatingCategory): Float =
        when (category) {
            TASTE -> taste
            PORTION_SIZE -> portionSize
            WORTHINESS -> worthiness
        } ?: 0f

    companion object {
        val empty = RatingCategories(null, null, null)
    }
}
