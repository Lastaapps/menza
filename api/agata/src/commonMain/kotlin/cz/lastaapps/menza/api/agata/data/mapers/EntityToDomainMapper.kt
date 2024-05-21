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

package cz.lastaapps.menza.api.agata.data.mapers

import agata.AddressEntity
import agata.ContactEntity
import agata.DishEntity
import agata.DishTypeEntity
import agata.InfoEntity
import agata.LinkEntity
import agata.NewsEntity
import agata.OpenTimeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import agata.StrahovEntity
import agata.SubsystemEntity
import cz.lastaapps.api.core.domain.model.Address
import cz.lastaapps.api.core.domain.model.Contact
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.Email
import cz.lastaapps.api.core.domain.model.Info
import cz.lastaapps.api.core.domain.model.LatLong
import cz.lastaapps.api.core.domain.model.Link
import cz.lastaapps.api.core.domain.model.LocationName
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Subsystem
import cz.lastaapps.api.core.domain.model.Message
import cz.lastaapps.api.core.domain.model.PhoneNumber
import cz.lastaapps.api.core.domain.model.PlaceOpeningInfo
import cz.lastaapps.api.core.domain.model.PlaceOpeningTime
import cz.lastaapps.api.core.domain.model.PlaceOpeningType
import cz.lastaapps.api.core.domain.model.ServingPlace
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DayOfWeek

internal fun List<SubsystemEntity>.toDomain() =
    sortedBy { it.itemOrder }
        .map { it.toDomain() }
        .toImmutableList()

private fun SubsystemEntity.toDomain() =
    Menza(
        Subsystem(id.toInt()),
        name,
        opened,
        supportsDaily,
        supportsWeekly,
        false,
        persistentListOf(),
    )

internal fun DishEntity.toDomain(
    pictograms: List<PictogramEntity>,
    servingPlaces: List<ServingPlaceEntity>,
) = let { dish ->
    Dish(
        amount = dish.amount,
        name = dish.fullName(),
        priceDiscounted = dish.priceDiscount?.toFloat(),
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
        ingredients = persistentListOf(),
        isActive = isActive,
    )
}

@Suppress("unused")
private fun DishEntity.fullName() =
    buildString {
        append(name ?: "")
        if (sideDishA != null || sideDishB != null) {
            append(" ")

            sideDishA?.let { append(it) }

            if (sideDishA != null && sideDishB != null) {
                append(" ")
            }

            sideDishB?.let { append(it) }
        }
    }

@Suppress("unused")
private fun DishEntity.fullNameSmart() =
    buildString {
        append(name)
        if (sideDishA != null || sideDishB != null) {
            append(" (")

            sideDishA?.let { append(it) }

            if (sideDishA != null && sideDishB != null) {
                append(" / ")
            }

            sideDishB?.let { append(it) }

            append(')')
        }
    }

internal fun DishTypeEntity?.toDomain(dishList: List<Dish>) =
    this?.let {
        DishCategory(
            nameShort = nameShort,
            name = nameLong,
            dishList = dishList.toImmutableList(),
        )
    } ?: DishCategory.other(dishList.toImmutableList())

internal fun InfoEntity?.toDomain(
    news: NewsEntity?,
    contacts: List<ContactEntity>,
    openingTimes: List<OpenTimeEntity>,
    links: List<LinkEntity>,
    address: AddressEntity?,
) = Info(
    header = news?.text?.let(::Message),
    footer = this?.footer?.let(::Message),
    contacts = contacts.map { it.toDomain() }.toImmutableList(),
    openingTimes = openingTimes.toDomain().toImmutableList(),
    links = links.map { it.toDomain() }.toImmutableList(),
    address = address?.let {
        Address(
            location = address.address.let(::LocationName),
            gps = LatLong(
                lat = address.lat.toFloat(),
                long = address.long.toFloat(),
            ),
        )
    },
)

private fun ContactEntity.toDomain() =
    Contact(
        role = role,
        name = name,
        phone = phone?.let(::PhoneNumber),
        email = email?.let(::Email),
    )

fun List<OpenTimeEntity>.toDomain() = this
    .groupBy { it.servingPlaceId }.entries
    .map { (_, entities1) ->
        val one = entities1.first()
        PlaceOpeningInfo(
            name = one.servingPlaceName,
            abbrev = one.servingPlaceAbbrev,
            types = entities1
                .groupBy { it.description }.entries
                .map { (description, entities2) ->
                    PlaceOpeningType(
                        description = description,
                        times = entities2.map {
                            PlaceOpeningTime(
                                startDay = it.dayFrom ?: DayOfWeek.MONDAY,
                                endDay = it.dayTo ?: it.dayFrom ?: DayOfWeek.MONDAY,
                                startTime = it.timeFrom,
                                endTime = it.timeTo,
                            )
                        }
                            .sortedBy { it.startDay }
                            .toImmutableList(),
                    )
                }.toImmutableList(),
        )
    }

private fun LinkEntity.toDomain() =
    Link(
        link = link,
        description = description,
    )

@JvmName("javaIsFuckingStupidShit")
internal fun List<StrahovEntity>.toDomain() =
    groupBy { it.groupId }
        .entries
        .sortedBy { it.value.first().groupOrder }
        .map { (_, values) ->
            val value = values.first()
            DishCategory(
                nameShort = null,
                name = value.groupName.trim(),
                dishList = values
                    .sortedBy { it.itemOrder }
                    .map { it.toDomain() }
                    .toImmutableList(),
            )
        }.toImmutableList()

private fun StrahovEntity.toDomain() = Dish(
    amount = amount,
    name = name,
    priceDiscounted = priceStudent.toFloat(),
    priceNormal = priceNormal.toFloat(),
    allergens = allergens.map { it.toInt() }.toImmutableList(),
    photoLink = photoLink,
    pictogram = persistentListOf(),
    servingPlaces = persistentListOf(),
    ingredients = persistentListOf(),
    isActive = true,
)
