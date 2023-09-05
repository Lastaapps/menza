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

package cz.lastaapps.api.core.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class Info(
    val header: Message?,
    val footer: Message?,
    val contacts: ImmutableList<Contact>,
    val openingTimes: ImmutableList<PlaceOpeningInfo>,
    val links: ImmutableList<Link>,
    val address: Address?,
) {
    companion object {
        val empty: Info
            get() = Info(
                null,
                null,
                persistentListOf(),
                persistentListOf(),
                persistentListOf(),
                null,
            )
    }
}

data class Contact(
    val role: String?,
    val name: String?,
    val phone: PhoneNumber?,
    val email: Email?,
)

data class Link(
    val link: String,
    val description: String,
)

data class PlaceOpeningInfo(
    val name: String,
    val abbrev: String,
    val types: ImmutableList<PlaceOpeningType>,
)

data class PlaceOpeningType(
    val description: String?,
    val times: ImmutableList<PlaceOpeningTime>,
)

data class PlaceOpeningTime(
    val startDay: DayOfWeek,
    val endDay: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
)

data class Address(
    val location: LocationName,
    val gps: LatLong?,
)

@JvmInline
value class Message(val text: String)

@JvmInline
value class LocationName(val name: String)

@JvmInline
value class Email(val mail: String)

@JvmInline
value class PhoneNumber(val number: String)

data class LatLong(val lat: Float, val long: Float)
