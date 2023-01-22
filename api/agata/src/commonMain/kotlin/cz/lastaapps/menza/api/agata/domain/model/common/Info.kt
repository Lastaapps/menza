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

package cz.lastaapps.menza.api.agata.domain.model.common

import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class Info(
    val header: String?,
    val footer: String?,
    val news: String?,
    val contacts: ImmutableList<Contact>,
    val openingTimes: ImmutableList<PlaceOpeningTime>,
    val link: String?,
    val gps: LogLat?,
    val address: String?,
)

data class Contact(
    val role: String?,
    val name: String?,
    val phone: String?,
    val email: String?,
)

data class PlaceOpeningTime(
    val placeName: String,
    val placeAbbrev: String,
    val placeOrder: Int,
    val description: String,
    val times: ImmutableList<OpeningTime>,
)

data class OpeningTime(
    val from: Pair<DayOfWeek, LocalTime>,
    val to: Pair<DayOfWeek, LocalTime>,
)

data class LogLat(val log: Float, val lat: Float)
