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

package cz.lastaapps.menza.api.agata.data

import arrow.core.right
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.domain.SyncProcessor
import cz.lastaapps.menza.api.agata.domain.model.SyncJobNoCache
import cz.lastaapps.menza.api.agata.domain.model.common.WeekDayDish
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.repo.WeekRepository
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
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
            outcome {
                val weeks = dishApi.getWeeks(subsystemId).bind()
                val week = weeks.firstOrNull()!! // TODO
                dishApi.getWeekDishList(week.id).bind()
            }
        },
        store = { data ->
            weekDishList.value = data.toDomain().toImmutableList()
        }
    )

    override suspend fun sync(): Outcome<Unit> =
        processor.run(syncJob)
}

internal object WeekDishRepoStrahovImpl : WeekRepository {
    override fun getData(): Flow<ImmutableList<WeekDayDish>> = flow { emit(persistentListOf()) }
    override suspend fun sync(): Outcome<Unit> = Unit.right()
}
