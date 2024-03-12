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

package cz.lastaapps.api.buffet.data.mappers

import buffet.DishEntity
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.WeekDishCategory
import cz.lastaapps.core.util.extensions.findDayOfWeek
import java.time.DayOfWeek.SATURDAY
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
                    name = dish.type,
                    dishList = persistentListOf(
                        // each dish has it's own category
                        Dish(
                            amount = null,
                            name = dish.name,
                            priceDiscounted = null,
                            priceNormal = dish.price.toFloat(),
                            allergens = null,
                            photoLink = null,
                            pictogram = persistentListOf(),
                            servingPlaces = persistentListOf(),
                            ingredients = dish.ingredients.toImmutableList(),
                            isActive = true,
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
        val monday = clock.now().toLocalDateTime(timeZone).date
            .findDayOfWeek(SATURDAY)
            .plus(2, DateTimeUnit.DAY)

        val offset = dayOfWeek.value - DayOfWeek.MONDAY.value
        val date = monday.plus(offset, DateTimeUnit.DAY)

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
                            priceDiscounted = null,
                            ingredients = dish.ingredients.toImmutableList(),
                        ),
                    ),
                )
            }.toImmutableList(),
        )
    }.toImmutableList()
