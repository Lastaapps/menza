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

import arrow.core.right
import arrow.core.rightIor
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.api.core.domain.sync.SyncJobNoCache
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.repo.WeekRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class WeekDishRepoImpl(
    private val subsystemId: Int,
    private val dishApi: DishApi,
    private val processor: SyncProcessor,
) : WeekRepository {

    private val weekDishList = MutableStateFlow<ImmutableList<WeekDayDish>>(persistentListOf())

    override fun getData(): Flow<ImmutableList<WeekDayDish>> = weekDishList

    private val syncJob = SyncJobNoCache(
        fetchApi = {
            val weeks = dishApi.getWeeks(subsystemId).bind()
            val week = weeks.firstOrNull()!! // TODO
            dishApi.getWeekDishList(week.id).bind()
        },
        convert = { data -> data.toDomain().rightIor() },
        store = { data ->
            weekDishList.value = data
        }
    )

    override suspend fun sync(): SyncOutcome =
        processor.run(syncJob)
}

internal object WeekDishRepoStrahovImpl : WeekRepository {
    override fun getData(): Flow<ImmutableList<WeekDayDish>> = flow { emit(persistentListOf()) }
    override suspend fun sync(): SyncOutcome = SyncResult.Unavailable.right()
}
