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

package cz.lastaapps.api.rating.data.repo

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.Rating
import cz.lastaapps.api.core.domain.sync.SyncSource
import cz.lastaapps.api.rating.data.repo.RatingRepository.Params
import cz.lastaapps.api.rating.domain.model.UserRating
import cz.lastaapps.core.domain.Outcome
import kotlinx.collections.immutable.ImmutableMap

internal interface RatingRepository : SyncSource<ImmutableMap<DishID, Rating>, Params> {
    suspend fun rate(rating: UserRating): Outcome<Unit>

    data class Params(
        val menza: MenzaType,
    )
}
