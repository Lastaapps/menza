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

import agata.StrahovEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.rightIor
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.TodayRepoParams
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.api.core.domain.validity.withParams
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.data.model.dto.StrahovDto
import cz.lastaapps.menza.api.agata.data.model.toDB
import cz.lastaapps.menza.api.agata.data.model.toDto
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
    private val processor: SyncProcessor<TodayRepoParams>,
    private val checker: ValidityChecker,
    private val beConfig: AgataBEConfig,
    hashStore: HashStore,
) : TodayDishRepo {
    private val log = localLogger()

    private val validityKey = ValidityKey.strahov()

    override fun getData(params: TodayRepoParams): Flow<ImmutableList<DishCategory>> =
        db.strahovQueries
            .get(params.language.toDB())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .combine(
                run {
                    checker
                        .isThisWeek(validityKey.withParams(params))
                        .onEach { log.i { "Validity changed to $it" } }
                },
            ) { data, validity ->
                data.takeIf { validity }.orEmpty()
            }.map { it.toDomain(params.language) }
            .onStart { log.d { "Starting collection" } }
            .onCompletion { log.d { "Completed collection" } }

    private val job =
        SyncJobHash<List<StrahovDto>, List<StrahovEntity>, TodayRepoParams>(
            hashStore = hashStore,
            hashType = { HashType.strahovHash().withLang(it.language) },
            getHashCode = {
                dishApi.getStrahovHash(it.language.toDto()).bind()
            },
            fetchApi = {
                dishApi.getStrahov(it.language.toDto()).bind().orEmpty()
            },
            convert = { params, data ->
                data.map { it.toEntity(beConfig, params.language) }.rightIor()
            },
            store = { params, data ->
                db.strahovQueries.deleteAll(params.language.toDB())
                data.forEach {
                    db.strahovQueries.insert(it)
                }
                log.d { "Data stored: ${data.size}" }
            },
        )

    override suspend fun sync(
        params: TodayRepoParams,
        isForced: Boolean,
    ): SyncOutcome =
        run {
            log.i { "Starting sync (f: $isForced)" }
            checker.withCheckRecent(validityKey, isForced) {
                processor.runSync(job, db, params, isForced = isForced)
            }
        }
}
