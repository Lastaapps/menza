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
import cz.lastaapps.api.core.domain.model.LatLong
import cz.lastaapps.api.core.domain.model.RequestLanguage
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
import cz.lastaapps.menza.api.agata.data.model.toDB
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

internal fun SubsystemDto.toEntity(lang: RequestLanguage) =
    SubsystemEntity(
        id = id.toLong(),
        name = name.trim(),
        opened = opened,
        supportsDaily = supportsDaily,
        supportsWeekly = supportsWeekly,
        itemOrder = order.toLong(),
        language = lang.toDB(),
    )

internal fun DishDto.toEntity(beConfig: AgataBEConfig, lang: RequestLanguage) =
    DishEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        typeId = typeId.toLong(),
        servingPlaces = servingPlaceList,
        amount = amount?.trim(),
        name = name?.trimDishName(),
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
        language = lang.toDB(),
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

internal fun DishTypeDto.toEntity(lang: RequestLanguage) =
    DishTypeEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        nameShort = nameShort,
        nameLong = nameLong,
        itemOrder = order.toLong(),
        language = lang.toDB(),
    )

internal fun PictogramDto.toEntity(lang: RequestLanguage) =
    PictogramEntity(
        id = id.toLong(),
        name = name?.trim() ?: "???",
        language = lang.toDB(),
    )

internal fun ServingPlaceDto.toEntity(lang: RequestLanguage) =
    ServingPlaceEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        name = name.trim(),
        description = description.trim(),
        abbrev = abbrev.trim(),
        language = lang.toDB(),
    )

internal fun InfoDto.toEntity(lang: RequestLanguage) =
    InfoEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        footer = footer?.removeHtml(),
        language = lang.toDB(),
    )

internal fun NewsDto.toEntity(subsystemId: Int, lang: RequestLanguage) =
    html.removeHtml().takeIfNotBlack()?.let { text ->
        NewsEntity(
            subsystemId = subsystemId.toLong(),
            text = text,
            language = lang.toDB(),
        )
    }

private fun String.removeHtml() = this
    .replace("<br>", "\n")
    .replace("<BR>", "\n")
    .replace("""<[^>]*>""".toRegex(), "")
    .trim()

internal fun ContactDto.toEntity(lang: RequestLanguage) =
    ContactEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        itemOrder = order.toLong(),
        role = role?.trim(),
        name = name?.trim(),
        phone = phone?.trim(),
        email = email?.trim(),
        language = lang.toDB(),
    )

internal fun OpenTimeDto.toEntity(lang: RequestLanguage) =
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
        language = lang.toDB(),
    )

private val czechDaysOfWeek = arrayOf("Po", "Út", "St", "Čt", "Pá", "So", "Ne")
private val englishDaysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private fun String.toDayOfWeek() = run {
    val index = czechDaysOfWeek.indexOf(this)
        .takeUnless { it < 0 }
        ?: englishDaysOfWeek.indexOf(this)
    DayOfWeek.of(index + 1)
}

private val timeRegex = """(\d+):(\d+)""".toRegex()
private fun String.toLocalTime() =
    timeRegex.find(this)?.let { match ->
        val (hours, minutes) = match.destructured
        LocalTime(hours.toInt(), minutes.toInt())
    }

internal fun LinkDto.toEntity(lang: RequestLanguage) =
    LinkEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        link = link,
        description = description.trim(),
        language = lang.toDB(),
    )

internal fun AddressDto.toEntity(lang: RequestLanguage) = run {
    val parsed = gps.toLatLong()
    AddressEntity(
        id = id.toLong(),
        subsystemId = subsystemId.toLong(),
        address = address.trim(),
        lat = parsed.lat.toDouble(),
        long = parsed.long.toDouble(),
        language = lang.toDB(),
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

internal fun StrahovDto.toEntity(beConfig: AgataBEConfig, lang: RequestLanguage) =
    StrahovEntity(
        id = id.toLong(),
        groupId = groupId.toLong(),
        groupName = groupName.myCapitalize(),
        groupOrder = groupOrder.toLong(),
        itemOrder = order.toLong(),
        amount = amount?.trim(),
        name = (name ?: EmptyNamePlaceholder).trim(),
        priceNormal = price.toDouble(),
        priceStudent = priceStudent.toDouble(),
        allergens = allergens,
        photoLink = photoLink?.let {
            beConfig.photoLinkForStrahov(it)
        },
        language = lang.toDB(),
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
