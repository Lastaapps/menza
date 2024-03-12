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

import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.WeekDishCategory
import cz.lastaapps.menza.api.agata.data.model.dto.WeekDishDto
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate

internal fun List<WeekDishDto>.toDomain() =
    groupBy { it.date }
        .entries
        .sortedBy { it.key }
        .map { (_, values) ->
            val value = values.first()
            WeekDayDish(
                date = value.date.parseDate(),
                categories = values.toCategory(),
            )
        }

private fun List<WeekDishDto>.toCategory() =
    groupBy { it.typeId }
        .entries
        .sortedBy { it.key }
        .map { (_, values) ->
            val value = values.first()
            WeekDishCategory(
                name = value.typeName.trim(),
                dishList = values.map { it.toDomain() }.toImmutableList(),
            )
        }
        .toImmutableList()

private val dateRegex = """(\d{4})-(\d+)-(\d+)""".toRegex()
private fun String.parseDate(): LocalDate =
    dateRegex.find(this)!!.destructured.let { (year, month, day) ->
        LocalDate(year.toInt(), month.toInt(), day.toInt())
    }

private fun WeekDishDto.toDomain() =
    WeekDish(
        name = name.trim(),
        amount = amount?.trim(),
        priceNormal = null,
        priceDiscounted = null,
        ingredients = persistentListOf(),
    )
