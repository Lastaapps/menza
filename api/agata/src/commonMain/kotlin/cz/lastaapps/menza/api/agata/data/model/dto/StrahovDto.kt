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

package cz.lastaapps.menza.api.agata.data.model.dto

import kotlinx.serialization.SerialName

/**
 * TMinutka
 */
@kotlinx.serialization.Serializable
internal data class StrahovDto(
    @SerialName("id")
    val id: Int,
    @SerialName("skupina_id")
    val groupId: Int,
    @SerialName("skupina_nazev")
    val groupNameCs: String,
    @SerialName("skupina_nazev_en")
    val groupNameEn: String,
    @SerialName("skupina_poradi")
    val groupOrder: Int,
    @SerialName("poradi")
    val order: Int,
    @SerialName("vaha")
    val amountCs: String?,
    @SerialName("vaha_en")
    val amountEn: String?,
    @SerialName("nazev")
    val nameCs: String?,
    @SerialName("nazev_en")
    val nameEn: String?,
    @SerialName("cena")
    val price: Float,
    @SerialName("cena_stud")
    val priceStudent: Float,
    @SerialName("alergeny")
    val allergens: List<Long>,
    @SerialName("foto")
    val photoLink: String?,
)
