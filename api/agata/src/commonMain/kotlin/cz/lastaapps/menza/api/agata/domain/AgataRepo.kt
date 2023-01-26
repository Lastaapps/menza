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

package cz.lastaapps.menza.api.agata.domain

import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.model.common.Info
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.core.domain.Outcome
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface AgataRepo {

    fun getIsReady(): Flow<Boolean>
    suspend fun syncRepo(): Outcome<Unit>

    fun getDishListFor(menza: Menza): Flow<ImmutableList<DishCategory>>
    suspend fun syncDishList(menza: Menza, force: Boolean = false): Outcome<Unit>

    fun getInfo(menza: Menza): Flow<Info>
    suspend fun syncInfo(menza: Menza, force: Boolean = false): Outcome<Unit>

    fun getWeek(date: LocalDate): Outcome<WeekDayDish>
}
