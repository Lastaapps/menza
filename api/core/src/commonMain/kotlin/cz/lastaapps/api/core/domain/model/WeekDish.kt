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

package cz.lastaapps.api.core.domain.model

import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate

data class WeekDayDish(
    val date: LocalDate,
    val categories: ImmutableList<WeekDishCategory>,
)

data class WeekDishCategory(
    val name: String,
    val dishList: ImmutableList<WeekDish>,
)

data class WeekDish(
    val name: String,
    val amount: String?,
    val priceNormal: Float?,
    val priceDiscounted: Float?,
)
