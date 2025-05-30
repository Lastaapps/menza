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

package cz.lastaapps.api.rating.domain.usecase

import cz.lastaapps.api.core.domain.FlowParametrizedCache
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.Rating
import cz.lastaapps.api.rating.data.repo.RatingRepository
import cz.lastaapps.api.rating.data.repo.RatingRepository.Params
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.flow.Flow

class GetDishRatingsUC internal constructor(
    context: UCContext,
    private val ratingRepository: RatingRepository,
) : UseCase(context) {
    private val cache = FlowParametrizedCache<ImmutableMap<DishID, Rating>, MenzaType>()

    suspend operator fun invoke(menza: MenzaType): Flow<ImmutableMap<DishID, Rating>> =
        launch {
            cache(menza) { menza ->
                ratingRepository.getData(Params(menza))
            }
        }
}
