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

import agata.DishEntity
import agata.DishTypeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import arrow.core.Tuple4
import arrow.core.rightIor
import co.touchlab.kermit.Logger
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.AgataBEConfig
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

internal class TodayDishSubsystemRepoImpl(
    private val subsystemId: Int,
    private val cafeteriaApi: CafeteriaApi,
    private val dishApi: DishApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
    private val beConfig: AgataBEConfig,
    hashStore: HashStore,
) : TodayDishRepo {

    private val log = Logger.withTag(this::class.simpleName + "($subsystemId)")

    private val validityKey = ValidityKey.agataToday(subsystemId)
    private val isValidFlow = checker.isFromToday(validityKey)
        .distinctUntilChanged()
        .onEach { log.i { "Validity changed to $it" } }

    override fun getData(): Flow<ImmutableList<DishCategory>> = channelFlow {
        // Get dish list
        db.dishQueries.getForSubsystem(subsystemId.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .combine(isValidFlow) { data, validity ->
                data.takeIf { validity }.orEmpty()
            }
            .collectLatest { dtoList ->
                log.i { "Starting flow combining" }

                // Finds flows that corresponds to each dish item
                val withInfo = dtoList.asSequence().map { entity ->
                    Tuple4(
                        entity,

                        // get dish type
                        db.dishTypeQueries.getByDishId(entity.typeId).asFlow()
                            .mapToOne(Dispatchers.IO),

                        // get dish pictogram
                        db.pictogramQueries.getByIds(entity.pictogram).asFlow()
                            .mapToList(Dispatchers.IO),

                        // Get dish serving places
                        db.servingPlaceQueries.getByIds(entity.servingPlaces).asFlow()
                            .mapToList(Dispatchers.IO),
                    )
                }

                // Combine flows together
                val combinedFlowList =
                    withInfo.map { (entity, typeFlow, pictogramFlow, servingFlow) ->
                        combine(typeFlow, pictogramFlow, servingFlow) { type, pictogram, serving ->
                            Tuple4(entity, type, pictogram, serving)
                        }
                    }

                // for an empty list an empty list will be returned
                val baseCase =
                    flow<PersistentList<Tuple4<DishEntity, DishTypeEntity, List<PictogramEntity>, List<ServingPlaceEntity>>>> {
                        emit(persistentListOf())
                    }

                val withInfoResolved = combinedFlowList.fold(baseCase) { acu, dataFlow ->
                    // joins all the flow into one huge + adding all the previous ones
                    combine(acu, dataFlow) { list, data ->
                        list.add(data)
                    }
                }

                val mapped = withInfoResolved.map { list ->
                    list
                        .map { (dish, type, pictogram, servingPlaces) ->
                            type to dish.toDomain(pictogram, servingPlaces)
                        }.groupBy { it.first }
                        .entries
                        .sortedBy { (key, _) -> key.itemOrder }
                        .map { (type, dishList) ->
                            type.toDomain(dishList.map { it.second })
                        }
                        .toImmutableList()
                }

                mapped.collectLatest { categories ->
                    log.i {
                        val dishCount = categories.sumOf { it.dishList.size }
                        "Collected ${categories.size} categories and $dishCount dishes"
                    }
                    send(categories)
                }
            }
    }.onStart {
        log.i { "Starting collection" }
    }.onCompletion {
        log.i { "Completed collection" }
    }

    private val dishListJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.dishHash(subsystemId),
        getHashCode = { dishApi.getDishesHash(subsystemId).bind() },
        fetchApi = { dishApi.getDishes(subsystemId).bind() },
        convert = { data -> data?.map { it.toEntity(beConfig) }.orEmpty().rightIor() },
        store = { data ->
            db.dishQueries.deleteSubsytem(subsystemId.toLong())
            data.forEach {
                db.dishQueries.insert(it)
            }
            log.d { "Stored dish list" }
        },
    )

    private val dishTypeJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.typesHash(subsystemId),
        getHashCode = { cafeteriaApi.getDishTypesHash(subsystemId).bind() },
        fetchApi = { cafeteriaApi.getDishTypes(subsystemId).bind() },
        convert = { data -> data?.map { it.toEntity() }.orEmpty().rightIor() },
        store = { data ->
            db.dishTypeQueries.deleteSubsystem(subsystemId.toLong())
            data.forEach {
                db.dishTypeQueries.insert(it)
            }
            log.d { "Stored dish type" }
        },
    )

    private val pictogramJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.pictogramHash(),
        getHashCode = { dishApi.getPictogramHash().bind() },
        fetchApi = { dishApi.getPictogram().bind().orEmpty() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.pictogramQueries.deleteAll()
            data.forEach {
                db.pictogramQueries.insert(it)
            }
            log.d { "Stored pictograms" }
        },
    )

    private val servingPlacesJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.servingPacesHash(subsystemId),
        getHashCode = { cafeteriaApi.getServingPlacesHash(subsystemId).bind() },
        fetchApi = { cafeteriaApi.getServingPlaces(subsystemId).bind().orEmpty() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.servingPlaceQueries.deleteSubsystem(subsystemId.toLong())
            data.forEach {
                db.servingPlaceQueries.insert(it)
            }
            log.d { "Stored serving places" }
        },
    )

    private val jobs = listOf(dishListJob, dishTypeJob, pictogramJob, servingPlacesJob)

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        checker.withCheckRecent(validityKey, isForced) {
            processor.runSync(jobs, db, isForced = isForced)
        }
    }
}
