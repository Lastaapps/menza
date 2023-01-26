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

import agata.AddressEntity
import agata.ContactEntity
import agata.DishEntity
import agata.DishTypeEntity
import agata.InfoEntity
import agata.LinkEntity
import agata.OpenTimeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import agata.SubsystemEntity
import cz.lastaapps.api.core.domain.model.MenzaType.Subsystem
import cz.lastaapps.api.core.domain.model.common.Contact
import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.model.common.Info
import cz.lastaapps.api.core.domain.model.common.Link
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.model.common.NewsHeader
import cz.lastaapps.api.core.domain.model.common.OpeningTime
import cz.lastaapps.api.core.domain.model.common.PlaceOpeningTime
import cz.lastaapps.api.core.domain.model.common.ServingPlace
import kotlinx.collections.immutable.toImmutableList

internal fun SubsystemEntity.toDomain() =
    Menza(Subsystem(id.toInt()), name, opened, isImportant)

internal fun DishEntity.toDomain(
    pictograms: List<PictogramEntity>,
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
        pictogram = pictograms.map(PictogramEntity::name).toImmutableList(),
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

internal fun InfoEntity?.toDomain(
    news: NewsHeader?,
    contacts: List<ContactEntity>,
    openingTimes: List<OpenTimeEntity>,
    links: List<LinkEntity>,
    address: AddressEntity?,
) = Info(
    header = this?.header,
    footer = this?.footer,
    news = news,
    contacts = contacts.map { it.toDomain() }.toImmutableList(),
    openingTimes = openingTimes.toDomain().toImmutableList(),
    links = links.map { it.toDomain() }.toImmutableList(),
    gps = address?.gps,
    address = address?.address,
)

private fun ContactEntity.toDomain() =
    Contact(
        role = role,
        name = name,
        phone = phone,
        email = email,
    )

private fun List<OpenTimeEntity>.toDomain() =
    groupBy { it.servingPlaceId }
        .entries
        .sortedBy { it.value.first().servingPlaceOrder }
        .map { (_, values) ->
            values.first().let { value ->
                PlaceOpeningTime(
                    placeName = value.servingPlaceName,
                    placeAbbrev = value.servingPlaceAbbrev,
                    description = value.description,
                    times = values
                        .sortedBy { it.itemOrder }
                        .map { it.toDomain() }
                        .toImmutableList(),
                )
            }
        }

private fun OpenTimeEntity.toDomain() =
    OpeningTime(
        from = dayFrom to timeFrom,
        to = dayTo to timeTo,
    )


private fun LinkEntity.toDomain() =
    Link(
        link = link,
        description = description,
    )
