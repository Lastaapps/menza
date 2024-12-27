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

package cz.lastaapps.api.rating.api

import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.RatingCategories
import cz.lastaapps.api.rating.data.model.RatingStateResponse
import cz.lastaapps.core.domain.Outcome

internal interface RatingAPI {
    suspend fun rate(
        menza: MenzaType,
        dishID: DishID,
        name: String,
        language: DataLanguage,
        rating: RatingCategories,
    ): Outcome<List<RatingStateResponse>>

    suspend fun getRatings(menza: MenzaType): Outcome<List<RatingStateResponse>>
}
