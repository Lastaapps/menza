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

package cz.lastaapps.menza.api.agata.domain.model.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
/**
 * TOtDoba
 */
data class OpenTime(
    @SerialName("id")
    val id: Int,
    @SerialName("podsystem_id")
    val subsystemId: Int,
    @SerialName("vydejna_id")
    val servingPlaceId: Int,
    @SerialName("vydejna_nazev")
    val servingPlaceName: String,
    @SerialName("vydejna_zkratka")
    val servingPlaceAbbrev: String,
    @SerialName("vydejna_poradi")
    val servingPlaceOrder: Int,
    @SerialName("od_popisek")
    val description: String,
    @SerialName("od_poradi")
    val order: Int,
    @SerialName("od_den_od")
    val dayFrom: String, // Po, Út, ...
    @SerialName("od_den_do")
    val dayTo: String,
    @SerialName("od_cas_od")
    val timeFrom: String, // HH:MM
    @SerialName("od_cas_do")
    val timeTo: String,
)
