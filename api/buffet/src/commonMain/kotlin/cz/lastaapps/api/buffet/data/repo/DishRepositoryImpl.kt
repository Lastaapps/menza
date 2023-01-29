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

package cz.lastaapps.api.buffet.data.repo

import arrow.core.None
import arrow.core.Some
import buffet.DishEntity
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import cz.lastaapps.api.buffet.BuffetDatabase
import cz.lastaapps.api.buffet.api.BuffetApi
import cz.lastaapps.api.buffet.domain.ValidityStore
import cz.lastaapps.api.buffet.domain.model.BuffetType
import cz.lastaapps.api.buffet.domain.model.dto.WebContentDto
import cz.lastaapps.api.buffet.domain.model.mappers.toDomainDays
import cz.lastaapps.api.buffet.domain.model.mappers.toDomainWeek
import cz.lastaapps.api.buffet.domain.model.mappers.toEntity
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.model.common.WeekDayDish
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.core.domain.OutcomeIor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

internal class DishLogicImpl(
    private val validityStore: ValidityStore,
    private val api: BuffetApi,
    private val db: BuffetDatabase,
    private val processor: SyncProcessor,
    private val clock: Clock,
) {
    fun getDataToday(type: BuffetType): Flow<ImmutableList<DishCategory>> =
        db.dishQueries.getForBuffetAndDayOfWeek(
            buffet = type,
            clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.dayOfWeek,
        )
            .asFlow()
            .mapToList()
            .map { it.toDomainDays() }
            .map { it.firstOrNull()?.second ?: emptyList() }
            .map { it.toImmutableList() }

    fun getDataWeek(type: BuffetType): Flow<ImmutableList<WeekDayDish>> =
        db.dishQueries.getForBuffet(type)
            .asFlow()
            .mapToList()
            .map { it.toDomainWeek(clock) }

    private val job = object : SyncJob<OutcomeIor<WebContentDto>, List<DishEntity>>(
        shouldRun = {
            if (validityStore.shouldReload()) Some {} else None
        },
        fetchApi = {
            api.process()
        },
        convert = { data ->
            data.map { it.toEntity() }
        },
        store = { data ->
            db.dishQueries.deleteAll()
            data.forEach {
                db.dishQueries.insert(it)
            }
        },
    ) {}

    suspend fun sync(): SyncOutcome = processor.runSync(job, db)
}

internal class WeekDishRepository(
    private val type: BuffetType,
    private val logic: DishLogicImpl,
) : WeekDishRepo {
    override fun getData(): Flow<ImmutableList<WeekDayDish>> = logic.getDataWeek(type)
    override suspend fun sync(): SyncOutcome = logic.sync()
}

internal class TodayDishRepository(
    private val type: BuffetType,
    private val logic: DishLogicImpl,
) : TodayDishRepo {
    override fun getData(): Flow<ImmutableList<DishCategory>> = logic.getDataToday(type)
    override suspend fun sync(): SyncOutcome = logic.sync()
}
