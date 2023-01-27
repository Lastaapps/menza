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

import agata.DishEntity
import agata.DishTypeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import arrow.core.Tuple4
import arrow.core.rightIor
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.HashType
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.repo.DishListRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.runSync
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.model.mapers.toEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class DishListRepoSubsystemImpl(
    private val subsystemId: Int,
    private val cafeteriaApi: CafeteriaApi,
    private val dishApi: DishApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val hashStore: HashStore,
) : DishListRepo {
    override fun getData(): Flow<ImmutableList<DishCategory>> = flow {
        // Get dish list
        db.dishQueries.getForSubsystem(subsystemId.toLong())
            .asFlow()
            .mapToList()
            .collectLatest { dtoList ->

                // Finds flows that corresponds to each dish item
                val withInfo = dtoList.asSequence().map { entity ->
                    Tuple4(
                        entity,

                        // get dish type
                        db.dishTypeQueries.getByDishId(entity.id).asFlow().mapToOneNotNull(),

                        // get dish piktogram
                        db.pictogramQueries.getByIds(entity.pictogram).asFlow().mapToList(),

                        // Get dish serving places
                        db.servingPlaceQueries.getByIds(entity.servingPlaces).asFlow()
                            .mapToList(),
                    )
                }

                // Combine flows together
                val combined = withInfo.map { (entity, typeFlow, pictogramFlow, servingFlow) ->
                    combine(typeFlow, pictogramFlow, servingFlow) { type, pictogram, serving ->
                        Tuple4(entity, type, pictogram, serving)
                    }
                }

                // for an empty list an empty list will be returned
                val baseCase =
                    flow<PersistentList<Tuple4<DishEntity, DishTypeEntity, List<PictogramEntity>, List<ServingPlaceEntity>>>> {
                        emit(persistentListOf())
                    }

                val withInfoResolved =
                    combined.fold(baseCase) { acu, dataFlow ->
                        // joins all the flow into one huge + adding all the previous ones
                        acu.combine(dataFlow) { list, data -> list.add(data) }
                    }

                val mapped = withInfoResolved.map { list ->
                    list.map { (dish, type, pictogram, servingPlaces) ->
                        type to dish.toDomain(pictogram, servingPlaces)
                    }.groupBy { it.first }
                        .entries
                        .sortedBy { (key, _) -> key.itemOrder }
                        .map { (type, dishList) ->
                            type.toDomain(dishList.map { it.second })
                        }
                        .toImmutableList()
                }

                mapped.collectLatest {
                    emit(it)
                }
            }
    }

    private val dishListJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.dishHash(subsystemId),
        getHashCode = { dishApi.getDishesHash(subsystemId).bind() },
        fetchApi = { dishApi.getDishes(subsystemId).bind() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.dishQueries.deleteSubsytem(subsystemId.toLong())
            data.forEach {
                db.dishQueries.insertEntity(it)
            }
        }
    )

    private val dishTypeJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.typesHash(subsystemId),
        getHashCode = { cafeteriaApi.getDishTypesHash(subsystemId).bind() },
        fetchApi = { cafeteriaApi.getDishTypes(subsystemId).bind() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.dishTypeQueries.deleteSubsystem(subsystemId.toLong())
            data.forEach {
                db.dishTypeQueries.insertEntity(it)
            }
        },
    )

    private val pictogramJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.pictogramHash(),
        getHashCode = { dishApi.getPictogramHash().bind() },
        fetchApi = { dishApi.getPictogram().bind() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.pictogramQueries.deleteAll()
            data.forEach {
                db.pictogramQueries.insertEntity(it)
            }
        }
    )

    private val servingPlacesJob = SyncJobHash(
        hashStore = hashStore,
        hashType = HashType.servingPacesHash(subsystemId),
        getHashCode = { cafeteriaApi.getServingPlacesHash(subsystemId).bind() },
        fetchApi = { cafeteriaApi.getServingPlaces(subsystemId).bind() },
        convert = { data -> data.map { it.toEntity() }.rightIor() },
        store = { data ->
            db.servingPlaceQueries.deleteSubsystem(subsystemId.toLong())
            data.forEach {
                db.servingPlaceQueries.insertEntity(it)
            }
        }
    )

    private val jobs = listOf(dishListJob, dishTypeJob, pictogramJob, servingPlacesJob)

    override suspend fun sync(): SyncOutcome =
        processor.runSync(jobs, db)
}
