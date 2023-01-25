/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.api.agata.domain.model.mapers

import cz.lastaapps.menza.api.agata.domain.model.common.Dish
import cz.lastaapps.menza.api.agata.domain.model.common.DishCategory
import cz.lastaapps.menza.api.agata.domain.model.dto.StrahovDto
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal fun List<StrahovDto>.toDomain() =
    groupBy { it.groupId }
        .entries
        .sortedBy { it.value.first().groupOrder }
        .map { (_, values) ->
            val value = values.first()
            DishCategory(
                nameShort = null,
                nameCs = value.nameCs.trim(),
                nameEn = value.nameEn.trim(),
                dishList = values.map { it.toDomain() }.toImmutableList(),
            )
        }

private fun StrahovDto.toDomain() = Dish(
    amountCs = amountCs,
    amountEn = amountEn,
    nameEn = nameEn,
    nameCs = nameCs,
    priceDiscount = priceStudent,
    priceNormal = price,
    allergens = allergens.parseAllergens().toImmutableList(),
    photoLink = photoLink,
    pictogram = null,
    servingPlaces = persistentListOf(),
)
