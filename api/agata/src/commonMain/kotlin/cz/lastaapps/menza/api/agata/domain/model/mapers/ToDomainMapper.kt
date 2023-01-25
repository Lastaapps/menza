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

import agata.DishEntity
import agata.DishTypeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import agata.SubsystemEntity
import cz.lastaapps.menza.api.agata.domain.model.MenzaType.Subsystem
import cz.lastaapps.menza.api.agata.domain.model.common.Dish
import cz.lastaapps.menza.api.agata.domain.model.common.DishCategory
import cz.lastaapps.menza.api.agata.domain.model.common.Menza
import cz.lastaapps.menza.api.agata.domain.model.common.ServingPlace
import cz.lastaapps.menza.api.agata.domain.model.common.fullName
import kotlinx.collections.immutable.toImmutableList

internal fun SubsystemEntity.toDomain() =
    Menza(Subsystem(id.toInt()), name, opened, isImportant)

internal fun DishEntity.toDomain(
    pictogram: PictogramEntity?,
    servingPlaces: List<ServingPlaceEntity>,
) = let { dish ->
    Dish(
        amountCs = dish.amount,
        amountEn = null,
        nameCs = dish.fullName(),
        nameEn = null,
        priceDiscount = dish.priceDiscount?.toFloat(),
        priceNormal = dish.priceNormal?.toFloat(),
        allergens = dish.allergens.map(Long::toInt).toImmutableList(),
        photoLink = dish.photoLink,
        pictogram = pictogram?.name,
        servingPlaces = servingPlaces.map { entity ->
            ServingPlace(
                name = entity.name,
                abbrev = entity.abbrev,
            )
        }.toImmutableList(),
    )
}

internal fun DishTypeEntity.toDomain(dishList: List<Dish>) =
    DishCategory(
        nameShort = nameShort,
        nameCs = nameLong,
        nameEn = null,
        dishList = dishList.toImmutableList(),
    )
