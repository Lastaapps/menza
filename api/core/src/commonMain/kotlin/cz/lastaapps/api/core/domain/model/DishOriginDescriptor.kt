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

import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishID
import kotlinx.serialization.Serializable

@Serializable
data class DishOriginDescriptor(
    val menza: MenzaType,
    val id: DishID,
    val language: DataLanguage,
    val name: String,
) {
    companion object {
        fun from(dish: Dish) =
            DishOriginDescriptor(
                menza = dish.menza,
                id = dish.id,
                language = dish.language,
                name = dish.name,
            )
    }

    @Suppress("SimplifyBooleanWithConstants")
    fun conforms(dish: Dish) =
        true &&
            id == dish.id &&
            name == dish.name &&
            menza == dish.menza &&
            language == dish.language &&
            true
}

fun Dish.toOrigin() = DishOriginDescriptor.from(this)
