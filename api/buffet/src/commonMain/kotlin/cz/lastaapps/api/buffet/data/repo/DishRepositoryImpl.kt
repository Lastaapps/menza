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

package cz.lastaapps.api.buffet.data.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.Some
import buffet.DishEntity
import co.touchlab.kermit.Logger
import cz.lastaapps.api.buffet.BuffetDatabase
import cz.lastaapps.api.buffet.api.BuffetApi
import cz.lastaapps.api.buffet.data.mappers.toDomainDays
import cz.lastaapps.api.buffet.data.mappers.toDomainWeek
import cz.lastaapps.api.buffet.data.mappers.toEntity
import cz.lastaapps.api.buffet.data.model.WebContentDto
import cz.lastaapps.api.buffet.domain.model.BuffetType
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.TodayRepoParams
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.core.domain.OutcomeIor
import cz.lastaapps.core.util.extensions.CET
import cz.lastaapps.core.util.extensions.findDayOfWeek
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime


internal class DishLogicImpl(
    private val api: BuffetApi,
    private val db: BuffetDatabase,
    private val processor: SyncProcessor<Unit>,
    private val clock: Clock,
    private val checker: ValidityChecker,
) {
        private val log = localLogger()

    private val validFrom = clock.now()
        .toLocalDateTime(TimeZone.CET).date
        .findDayOfWeek(DayOfWeek.SATURDAY)
        .let { LocalDateTime(it, LocalTime(12, 0)) }
        .toInstant(TimeZone.CET)

    private val validityKey = ValidityKey.buffetDish()
    private val hasValidData = checker.isUpdatedSince(validityKey, validFrom)
        .onEach { log.i { "Validity changed to $it" } }

    fun getDataToday(type: BuffetType): Flow<ImmutableList<DishCategory>> =
        db.dishQueries.getForBuffetAndDayOfWeek(
            buffet = type,
            clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.dayOfWeek,
        )
            .asFlow()
            .mapToList(Dispatchers.IO)
            .combine(hasValidData) { data, validity ->
                data.takeIf { validity }.orEmpty()
            }
            .map { it.toDomainDays() }
            .map { it.firstOrNull()?.second ?: emptyList() }
            .map { it.toImmutableList() }

    fun getDataWeek(type: BuffetType): Flow<ImmutableList<WeekDayDish>> =
        db.dishQueries.getForBuffet(type)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .combine(hasValidData) { data, validity ->
                data.takeIf { validity }.orEmpty()
            }
            .map { it.toDomainWeek(clock) }

    private val job = object : SyncJob<OutcomeIor<WebContentDto>, List<DishEntity>, Unit>(
        shouldRun = { _, _ -> Some {} },
        fetchApi = {
            api.process()
        },
        convert = { _, data ->
            data.map { it.toEntity() }
        },
        store = { _, data ->
            db.dishQueries.deleteAll()
            data.forEach {
                db.dishQueries.insert(it)
            }
            log.d { "Data stored" }
        },
    ) {}

    suspend fun sync(isForced: Boolean): SyncOutcome {
        return checker.withCheckSince(validityKey, isForced, validFrom) {
            processor.runSync(job, db, Unit, isForced = isForced)
        }
    }
}

internal class WeekDishRepository(
    private val type: BuffetType,
    private val logic: DishLogicImpl,
) : WeekDishRepo {
    private val log = Logger.withTag(this::class.simpleName + "($type)")
    override fun getData(params: TodayRepoParams): Flow<ImmutableList<WeekDayDish>> =
        logic.getDataWeek(type)
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(params: TodayRepoParams, isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        logic.sync(isForced)
    }
}

internal class TodayDishRepository(
    private val type: BuffetType,
    private val logic: DishLogicImpl,
) : TodayDishRepo {
    private val log = Logger.withTag(this::class.simpleName + "($type)")

    override fun getData(params: TodayRepoParams): Flow<ImmutableList<DishCategory>> =
        logic.getDataToday(type)
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(params: TodayRepoParams, isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        logic.sync(isForced)
    }
}
