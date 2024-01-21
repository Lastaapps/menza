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

package cz.lastaapps.menza.api.agata.data.repo

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.rightIor
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

internal class TodayDishStrahovRepoImpl(
    private val dishApi: DishApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
    private val beConfig: AgataBEConfig,
    hashStore: HashStore,
) : TodayDishRepo {

    private val log = localLogger()

    private val validityKey = ValidityKey.strahov()
    private val isValidFlow = checker.isThisWeek(validityKey)
        .onEach { log.i { "Validity changed to $it" } }

    override fun getData(): Flow<ImmutableList<DishCategory>> =
        db.strahovQueries
            .get()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .combine(isValidFlow) { data, validity ->
                data.takeIf { validity }.orEmpty()
            }
            .map { it.toDomain() }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

    private val job = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.strahovHash(),
        getHashCode = { dishApi.getStrahovHash().bind() },
        fetchApi = { dishApi.getStrahov().bind().orEmpty() },
        convert = { data -> data.map { it.toEntity(beConfig) }.rightIor() },
        store = { data ->
            db.strahovQueries.deleteAll()
            data.forEach {
                db.strahovQueries.insert(it)
            }
            log.d { "Data stored" }
        },
    )

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        checker.withCheckRecent(validityKey, isForced) {
            processor.runSync(job, isForced = isForced)
        }
    }
}
