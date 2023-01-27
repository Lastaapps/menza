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
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.HashType
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.repo.MenzaListRepo
import cz.lastaapps.api.core.domain.sync.SyncJobHash
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.domain.model.dto.SubsystemDto
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.model.mapers.toEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class MenzaListRepoImpl(
    private val api: CafeteriaApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
) : MenzaListRepo {

    override fun getData(): Flow<ImmutableList<Menza>> =
        db.subsystemQueries.getAll()
            .asFlow()
            .mapToList()
            .map { it.map { item -> item.toDomain() } }
            .map { it.toPersistentList() }
            .map { it.add(MenzaType.Strahov.instance) }

    private val subsystemJob =
        SyncJobHash(
            hashType = HashType.subsystemHash(),
            getHashCode = { api.getSubsystemsHash().bind() },
            fetchApi = {
                val subsystems = api.getSubsystems().bind()
                val importantIds = subsystems.map(SubsystemDto::id)
                val allSubsystems = api.getAllSubsystems().bind()
                allSubsystems.map {
                    (it.id in importantIds) to it
                }
            },
            convert = { dtos ->
                dtos.map { (important, dto) ->
                    dto.toEntity(isImportant = important)
                }.rightIor()
            },
            store = { result ->
                db.subsystemQueries.deleteAll()
                result.forEach { dto ->
                    db.subsystemQueries.insertEntity(dto)
                }
            },
        )

    override suspend fun sync(): SyncOutcome =
        processor.run(subsystemJob)
}

