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

package cz.lastaapps.menza.api.agata.data.model.dto

import kotlinx.serialization.SerialName

/**
 * TKontakt
 */
@kotlinx.serialization.Serializable
internal data class ContactDto(
    @SerialName("id")
    val id: Int,
    @SerialName("podsystem_id")
    val subsystemId: Int,
//    @SerialName("maps")
//    val gps: String,
    @SerialName("poradi")
    val order: Int,
    @SerialName("pozice")
    val role: String?,
    @SerialName("jmeno")
    val name: String?,
    @SerialName("telefon")
    val phone: String?,
    @SerialName("email")
    val email: String?,
)
