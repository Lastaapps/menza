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

package cz.lastaapps.menza.features.today.ui.model

import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.rating.data.model.DishRatingDescriptor
import kotlinx.serialization.Serializable

// This class exists because dish have no ID in the app and cannot be serialized
// (they can be but it's a bad practise on this layer). This class is used in navigation.
@Serializable
class DishForRating private constructor(
    private val menza: MenzaType,
    private val id: String,
    private val language: DataLanguage,
    val name: String,
) {
    companion object {
        fun from(dish: Dish) =
            DishForRating(
                menza = dish.menza,
                id = dish.id.value,
                language = dish.language,
                name = dish.name,
            )
    }

    fun toDishRatingDescriptor() =
        DishRatingDescriptor(
            menza = menza,
            id = DishID(id),
            language = language,
            name = name,
        )
}
