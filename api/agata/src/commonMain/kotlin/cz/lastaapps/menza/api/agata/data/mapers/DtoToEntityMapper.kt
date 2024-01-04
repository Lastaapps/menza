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
import agata.StrahovEntiy
import agata.SubsystemEntity
import cz.lastaapps.api.core.domain.model.LatLong
import cz.lastaapps.core.util.extensions.takeIfNotBlack
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.dto.AddressDto
import cz.lastaapps.menza.api.agata.data.model.dto.ContactDto
import cz.lastaapps.menza.api.agata.data.model.dto.DishDto
import cz.lastaapps.menza.api.agata.data.model.dto.DishTypeDto
import cz.lastaapps.menza.api.agata.data.model.dto.InfoDto
import cz.lastaapps.menza.api.agata.data.model.dto.LinkDto
import cz.lastaapps.menza.api.agata.data.model.dto.NewsDto
import cz.lastaapps.menza.api.agata.data.model.dto.OpenTimeDto
import cz.lastaapps.menza.api.agata.data.model.dto.PictogramDto
import cz.lastaapps.menza.api.agata.data.model.dto.ServingPlaceDto
import cz.lastaapps.menza.api.agata.data.model.dto.StrahovDto
import cz.lastaapps.menza.api.agata.data.model.dto.SubsystemDto
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

internal fun SubsystemDto.toEntity() =
    SubsystemEntity(
        id = id.toLong(),
        name = name.trim(),
        opened = opened,
        supportsDaily = supportsDaily,
        supportsWeekly = supportsWeekly,
        itemOrder = order.toLong(),
    )

internal fun DishDto.toEntity(beConfig: AgataBEConfig) =
    DishEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        typeId = typeId.toLong(),
        servingPlaces = servingPlaceList,
        amount = amount?.trim(),
        name = name.trimDishName(),
        sideDishA = sideDishA?.trimDishName(),
        sideDishB = sideDishB?.trimDishName(),
        priceNormal = priceNormal.toDouble(),
        priceDiscount = priceDiscount.toDouble(),
        allergens = allergens,
        photoLink = photoLink?.let {
            beConfig.photoLinkForAgataSubsystem(subsystemId, it)
        },
        pictogram = pictogram,
        isActive = isActive,
    )

private val invalidCharacters = arrayOf('(', ')', '[', ']', '\\', '/', '|', '.', '-', '_')
private fun String.trimDishName() = this
    .trim()
//    .dropWhile { it == ',' }
//    .dropLastWhile { it == ',' }
    .map { if (it in invalidCharacters) ' ' else it }
    .joinToString(separator = "")
    .replace("""\s*,\s*""".toRegex(), ", ")
    .replace("""\s+""".toRegex(), " ")

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
        name = name.trim(),
    )

internal fun ServingPlaceDto.toEntity() =
    ServingPlaceEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        name = name.trim(),
        description = description.trim(),
        abbrev = abbrev.trim(),
    )

internal fun InfoDto.toEntity() =
    InfoEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        footer = footer?.removeHtml(),
    )

internal fun NewsDto.toEntity(subsystemId: Int) =
    html.removeHtml().takeIfNotBlack()?.let { text ->
        NewsEntity(
            subsystemId = subsystemId.toLong(),
            text = text,
        )
    }

private fun String.removeHtml() = this
    .replace("<br>", "\n")
    .replace("<BR>", "\n")
    .replace("""<[^>]*>""".toRegex(), "")
    .trim()

internal fun ContactDto.toEntity() =
    ContactEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        itemOrder = order.toLong(),
        role = role?.trim(),
        name = name?.trim(),
        phone = phone?.trim(),
        email = email?.trim(),
    )

internal fun OpenTimeDto.toEntity() =
    OpenTimeEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        servingPlaceId = servingPlaceId.toLong(),
        servingPlaceName = servingPlaceName.trim(),
        servingPlaceAbbrev = servingPlaceAbbrev.trim(),
        servingPlaceOrder = servingPlaceOrder.toLong(),
        description = description?.trim(),
        itemOrder = order.toLong(),
        dayFrom = dayFrom?.toDayOfWeek(),
        dayTo = (dayTo ?: dayFrom)?.toDayOfWeek(),
        timeFrom = timeFrom.toLocalTime()!!,
        timeTo = timeTo?.toLocalTime() ?: timeFrom.toLocalTime()!!,
    )

private val czechDaysOfWeek = arrayOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
private fun String.toDayOfWeek() =
    DayOfWeek.of(czechDaysOfWeek.indexOf(this) + 1)

private val timeRegex = """(\d+):(\d+)""".toRegex()
private fun String.toLocalTime() =
    timeRegex.find(this)?.let { match ->
        val (hours, minutes) = match.destructured
        LocalTime(hours.toInt(), minutes.toInt())
    }

internal fun LinkDto.toEntity() =
    LinkEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        link = link,
        description = description.trim(),
    )

internal fun AddressDto.toEntity() = run {
    val parsed = gps.toLatLong()
    AddressEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        address = address.trim(),
        lat = parsed.lat.toDouble(),
        long = parsed.long.toDouble(),
    )
}

private fun String.toLatLong() =
    this
        .split(",")
        .map { it.toFloat() }
        .let { (lat, long) ->
            LatLong(lat = lat, long = long)
        }


// shown only if dish has neither Czech nor English name
private const val EmptyNamePlaceholder = """"¯\(°_o)/¯"""

internal fun StrahovDto.toEntity(beConfig: AgataBEConfig) =
    StrahovEntiy(
        id = id.toLong(),
        groupId = groupId.toLong(),
        groupNameCs = groupNameCs.myCapitalize(),
        groupNameEn = groupNameEn.myCapitalize(),
        groupOrder = groupOrder.toLong(),
        itemOrder = order.toLong(),
        amountCs = (amountCs ?: amountEn)?.trim(),
        amountEn = (amountEn ?: amountCs)?.trim(),
        nameCs = (nameCs ?: nameEn ?: EmptyNamePlaceholder).trim(),
        nameEn = (nameEn ?: nameCs ?: EmptyNamePlaceholder).trim(),
        priceNormal = price.toDouble(),
        priceStudent = priceStudent.toDouble(),
        allergens = allergens,
        photoLink = photoLink?.let {
            beConfig.photoLinkForStrahov(it)
        },
    )

// Strahov uses ALL CAPS and it looks just horrible
private fun String.myCapitalize() =
    mapIndexed { index, c ->
        if (index == 0) {
            c.uppercaseChar()
        } else {
            c.lowercaseChar()
        }
    }.joinToString(separator = "")
