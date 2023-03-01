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

import arrow.core.recover
import arrow.core.right
import arrow.core.rightIor
import arrow.fx.coroutines.parMap
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import cz.lastaapps.api.core.domain.sync.SyncJobNoCache
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult.Unavailable
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.core.domain.error.ApiError.WeekNotAvailable
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import kotlin.time.Duration.Companion.days
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.lighthousegames.logging.logging

internal class WeekDishRepoImpl(
    private val subsystemId: Int,
    private val dishApi: DishApi,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
) : WeekDishRepo {

    private val log = logging(this::class.simpleName + "($subsystemId)")

    private val validityKey = ValidityKey.agataWeek(subsystemId)
    private val isValidFlow = checker.isThisWeek(validityKey)
        .onEach { log.i { "Validity changed to $it" } }

    private val weekDishList = MutableStateFlow<ImmutableList<WeekDayDish>>(persistentListOf())

    override fun getData(): Flow<ImmutableList<WeekDayDish>> =
        combine(
            weekDishList,
            isValidFlow,
        ) { data, validity ->
            data.takeIf { validity } ?: persistentListOf()
        }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

//    private fun List<WeekDto>.selectCurrent(timeZone: TimeZone = TimeZone.currentSystemDefault()): WeekDto? {
//        if (isEmpty()) return null
//        if (size == 1 && first().id == 0) return null
//        val now = clock.now().toLocalDateTime(timeZone).date
//        return if (now.dayOfWeek <= FRIDAY) first() else last()
//    }

    private val syncJob = SyncJobNoCache(
        fetchApi = {
            val weeks = dishApi.getWeeks(subsystemId).bind()
                ?: raise(WeekNotAvailable)

            weeks
                .sortedBy { it.id }
                .parMap { week ->
                    dishApi.getWeekDishList(week.id).bind().orEmpty()
                }.flatten()
        },
        convert = { data -> data.toDomain().rightIor() },
        store = { data ->
            weekDishList.value = data
        }
    )

    private var hasSynced = false
    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced, h: $hasSynced)" }

        val myForced = isForced || !hasSynced
        checker.withCheckSince(validityKey, myForced, 1.days) {
            processor.runSync(syncJob, isForced = myForced).recover {
                if (it is WeekNotAvailable) Unavailable else raise(it)
            }
        }.onRight { hasSynced = true }
    }
}

internal object WeekRepoStrahovImpl : WeekDishRepo {
    private val log = logging()

    override fun getData(): Flow<ImmutableList<WeekDayDish>> =
        flow { emit(persistentListOf<WeekDayDish>()) }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        Unavailable.right()
    }
}
