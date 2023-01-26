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

/**
 * TInfo
 */
@kotlinx.serialization.Serializable
internal class InfoDto(
    @SerialName("id")
    val id: Int,
    @SerialName("podsystem_id")
    val subsystemId: Int,
//    @SerialName("podsystem_web")
//    val subsystemWeb: String, // name
    @SerialName("text_nahore")
    val header: String?,
    @SerialName("text_dole")
    val footer: String?,
//    @SerialName("menu")
//    val menuType: Int, // [0 - lunch, 1 - dinner]
)
