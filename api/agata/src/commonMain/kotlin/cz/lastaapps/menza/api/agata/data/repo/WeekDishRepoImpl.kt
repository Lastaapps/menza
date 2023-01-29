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

package cz.lastaapps.menza.api.agata.data.repo

import arrow.core.left
import arrow.core.recover
import arrow.core.right
import arrow.core.rightIor
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.sync.SyncJobNoCache
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult.Unavailable
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.core.domain.error.ApiErrorLogic.WeekNotAvailable
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.domain.model.dto.WeekDto
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import java.time.DayOfWeek.FRIDAY
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class WeekDishRepoImpl(
    private val subsystemId: Int,
    private val dishApi: DishApi,
    private val processor: SyncProcessor,
    private val clock: Clock,
) : WeekDishRepo {

    private val weekDishList = MutableStateFlow<ImmutableList<WeekDayDish>>(persistentListOf())

    override fun getData(): Flow<ImmutableList<WeekDayDish>> = weekDishList

    private fun List<WeekDto>.selectCurrent(timeZone: TimeZone = TimeZone.currentSystemDefault()): WeekDto? {
        if (isEmpty()) return null
        if (size == 1 && first().id == 0) return null
        val now = clock.now().toLocalDateTime(timeZone).date
        return if (now.dayOfWeek <= FRIDAY) first() else last()
    }

    private val syncJob = SyncJobNoCache(
        fetchApi = {
            val weeks = dishApi.getWeeks(subsystemId).bind()
            val week = weeks.selectCurrent() ?: raise(WeekNotAvailable)
            dishApi.getWeekDishList(week.id).bind() ?: emptyList()
        },
        convert = { data -> data.toDomain().rightIor() },
        store = { data ->
            weekDishList.value = data
        }
    )

    override suspend fun sync(): SyncOutcome =
        processor.runSync(syncJob).recover {
            if (it is WeekNotAvailable) {
                Unavailable.right()
            } else {
                it.left()
            }.bind()
        }
}

internal object WeekRepoStrahovImpl : WeekDishRepo {
    override fun getData(): Flow<ImmutableList<WeekDayDish>> = flow { emit(persistentListOf()) }
    override suspend fun sync(): SyncOutcome = Unavailable.right()
}
