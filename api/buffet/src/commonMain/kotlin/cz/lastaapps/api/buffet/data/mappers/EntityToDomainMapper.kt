/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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
import cz.lastaapps.api.core.domain.model.DataLanguage.Czech
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.model.WeekDish
import cz.lastaapps.api.core.domain.model.WeekDishCategory
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.core.util.extensions.findDayOfWeek
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek.SATURDAY

internal fun List<DishEntity>.toDomainDays(menzaType: MenzaType.Buffet): List<Pair<DayOfWeek, List<DishCategory>>> =
    groupBy { it.dayOfWeek }
        .entries
        .sortedBy { it.key }
        .map { (dayOfWeek, dayDishList) ->
            dayOfWeek to
                dayDishList
                    .groupBy { it.type }
                    .map { (type, dishes) ->
                        DishCategory(
                            nameShort = null,
                            name = type,
                            dishList =
                                dishes
                                    .map { dish ->
                                        // each dish has it's own category
                                        Dish(
                                            menzaType,
                                            id = DishID(dish.name),
                                            language = Czech,
                                            amount = null,
                                            name = dish.name,
                                            priceDiscounted = null,
                                            priceNormal = dish.price.toFloat(),
                                            allergens = null,
                                            photoLink = null,
                                            pictogram = persistentListOf(),
                                            servingPlaces = persistentListOf(),
                                            isActive = true,
                                        )
                                    }.toPersistentList(),
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
        val monday =
            clock
                .now()
                .toLocalDateTime(timeZone)
                .date
                .findDayOfWeek(SATURDAY)
                .plus(2, DateTimeUnit.DAY)

        val offset = dayOfWeek.value - DayOfWeek.MONDAY.value
        val date = monday.plus(offset, DateTimeUnit.DAY)

        WeekDayDish(
            date = date,
            categories =
                dayDishList
                    .groupBy { it.type }
                    .map { (type, dishes) ->
                        WeekDishCategory(
                            name = type,
                            dishList =
                                dishes
                                    .map { dish ->
                                        // each dish has it's own category
                                        WeekDish(
                                            name = dish.name,
                                            amount = null,
                                            priceNormal = dish.price.toFloat(),
                                            priceDiscounted = null,
                                        )
                                    }.toPersistentList(),
                        )
                    }.toImmutableList(),
        )
    }.toImmutableList()
