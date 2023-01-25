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
import cz.lastaapps.core.util.takeIfNotBlack
import cz.lastaapps.menza.api.agata.domain.model.dto.DishDto
import cz.lastaapps.menza.api.agata.domain.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.domain.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.domain.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.domain.model.dto.SubsystemDto

internal fun SubsystemDto.toEntity(isImportant: Boolean) =
    SubsystemEntity(
        id = id.toLong(),
        name = name,
        opened = opened == 1,
        isImportant = isImportant,
    )

internal fun DishDto.toEntity() =
    DishEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        typeId = typeId.toLong(),
        servingPlaces = servingPlaceList.split(';').map { it.toLong() },
        amount = amount,
        name = name,
        sideDishA = sideDishA,
        sideDishB = sideDishB,
        priceNormal = priceNormal.toDouble(),
        priceDiscount = priceDiscount.toDouble(),
        allergens = parseAllergens(),
        photoLink = photoLink.takeIfNotBlack(),
        pictogram = pictogram.toLong(),
        isActive = isActive,
    )


private fun DishDto.parseAllergens() =
    allergens
        .split(',', ' ', '.' /*just for sure*/, ';', '-', '_', '|')
        .filter { it.isNotBlank() }
        .mapNotNull { it.toLongOrNull() }

internal fun DishTypeDto.toEntity() =
    DishTypeEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        nameShort = nameShort,
        nameLong = nameLong,
        itemOrder = order.toLong(),
    )

internal fun PictogramDto.toEntity() =
    PictogramEntity(
        id = id.toLong(),
        name = name,
    )

internal fun ServingPlaceDto.toEntity() =
    ServingPlaceEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        name = name,
        description = description,
        abbrev = abbrev,
    )
