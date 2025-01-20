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

package cz.lastaapps.api.buffet.data.mappers

import buffet.DishEntity
import cz.lastaapps.api.buffet.data.model.DishDayDto
import cz.lastaapps.api.buffet.data.model.WebContentDto
import cz.lastaapps.api.buffet.domain.model.BuffetType
import cz.lastaapps.api.buffet.domain.model.BuffetType.FEL
import cz.lastaapps.api.buffet.domain.model.BuffetType.FS

internal fun WebContentDto.toEntity() = fs.toEntity(FS) + fel.toEntity(FEL)

private fun List<DishDayDto>.toEntity(buffet: BuffetType) =
    map { day ->
        val dayOfWeek = day.dayOfWeek
        day.dishList.map { dish ->
            DishEntity(
                buffet = buffet,
                dayOfWeek = dayOfWeek,
                type = dish.type,
                name = dish.name,
                price = dish.price.toLong(),
                itemOrder = dish.order.toLong(),
            )
        }
    }.flatten()
