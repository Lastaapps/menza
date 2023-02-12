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

package cz.lastaapps.api.buffet.domain.model.mappers

import buffet.DishEntity
import cz.lastaapps.api.core.domain.model.common.Dish
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.api.core.domain.model.common.WeekDish
import cz.lastaapps.api.core.domain.model.common.WeekDishCategory
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

internal fun List<DishEntity>.toDomainDays(): List<Pair<DayOfWeek, List<DishCategory>>> =
    groupBy { it.dayOfWeek }
        .entries
        .sortedBy { it.key }
        .map { (dayOfWeek, dayDishList) ->
            dayOfWeek to dayDishList.map { dish ->
                DishCategory(
                    nameShort = null,
                    nameCs = dish.type,
                    nameEn = null,
                    dishList = persistentListOf(
                        // each dish has it's own category
                        Dish(
                            amountCs = null,
                            amountEn = null,
                            nameEn = null,
                            nameCs = dish.name,
                            priceDiscountFloat = null,
                            priceNormalFloat = dish.price.toFloat(),
                            allergens = null,
                            photoLink = null,
                            pictogram = persistentListOf(),
                            servingPlaces = persistentListOf(),
                            ingredients = dish.ingredients.toImmutableList(),
                        ),
                    ),
                )
            }
        }

internal fun List<DishEntity>.toDomainWeek(
    clock: Clock,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) = groupBy { it.dayOfWeek }
    .entries
    .sortedBy { it.key }
    .map { (dayOfWeek, dayDishList) ->
        val now = clock.now().toLocalDateTime(timeZone).date
        //  - (now - monday) + dof
        val offset = -(dayOfWeek.value - DayOfWeek.MONDAY.value) + now.dayOfWeek.value
        val date = now.plus(offset, DateTimeUnit.DAY)

        WeekDayDish(
            date = date,
            categories = dayDishList.map { dish ->
                WeekDishCategory(
                    name = dish.type,
                    dishList = persistentListOf(
                        // each dish has it's own category
                        WeekDish(
                            name = dish.name,
                            amount = null,
                            priceNormal = dish.price.toFloat(),
                            ingredients = dish.ingredients.toImmutableList(),
                        ),
                    ),
                )
            }.toImmutableList(),
        )
    }.toImmutableList()
