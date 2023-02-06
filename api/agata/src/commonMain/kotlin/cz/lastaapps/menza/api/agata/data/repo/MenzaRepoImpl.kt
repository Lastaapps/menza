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

import arrow.core.right
import arrow.core.rightIor
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.HashType
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.model.mapers.toEntity
import kotlin.time.Duration.Companion.days
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class MenzaSubsystemRepoImpl(
    private val api: CafeteriaApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
    hashStore: HashStore,
) : MenzaRepo {
    override val isReady: Flow<Boolean> =
        db.subsystemQueries.getAll()
            .asFlow()
            .mapToList()
            .map { it.isNotEmpty() }

    override fun getData(): Flow<ImmutableList<Menza>> =
        db.subsystemQueries.getAll()
            .asFlow()
            .mapToList()
            .map { it.toDomain() }

    private val subsystemJob =
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.subsystemHash(),
            getHashCode = { api.getSubsystemsHash().bind() },
            fetchApi = {
                api.getSubsystems().bind().orEmpty()
            },
            convert = { dtos ->
                dtos.map { dto ->
                    dto.toEntity()
                }.rightIor()
            },
            store = { result ->
                db.subsystemQueries.deleteAll()
                result.forEach { dto ->
                    db.subsystemQueries.insert(dto)
                }
            },
        )

    override suspend fun sync(isForced: Boolean): SyncOutcome =
        checker.withCheckSince(ValidityKey.agataMenza(), isForced, 7.days) {
            processor.runSync(subsystemJob, db, isForced)
        }
}

internal object MenzaStrahovRepoImpl : MenzaRepo {
    override val isReady: Flow<Boolean> = MutableStateFlow(true)

    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        @Suppress("SpellCheckingInspection")
        persistentListOf(
            Menza(
                type = Strahov,
                name = "Restaurace Strahov",
                isOpened = true,
                supportsDaily = true,
                supportsWeekly = false,
            )
        ).let { emit(it) }
    }

    override suspend fun sync(isForced: Boolean): SyncOutcome = SyncResult.Skipped.right()
}
