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

import arrow.core.rightIor
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.sync.SyncJobNoCache
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

internal class TodayDishStrahovRepoImpl(
    private val dishApi: DishApi,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
) : TodayDishRepo {

    private val validityKey = ValidityKey.strahovToday()
    private val isValidFlow = checker.isFromToday(validityKey)

    private val dishList = MutableStateFlow<ImmutableList<DishCategory>>(persistentListOf())

    override fun getData(): Flow<ImmutableList<DishCategory>> = dishList
        .combine(isValidFlow) { data, validity ->
            data.takeIf { validity } ?: persistentListOf()
        }

    private val job = SyncJobNoCache(
        fetchApi = { dishApi.getStrahov().bind() },
        convert = { it.toDomain().toImmutableList().rightIor() },
        store = { data ->
            dishList.value = data
        }
    )

    override suspend fun sync(isForced: Boolean): SyncOutcome =
        checker.withCheckRecent(validityKey, isForced) {
            processor.runSync(job, isForced = isForced)
        }
}
