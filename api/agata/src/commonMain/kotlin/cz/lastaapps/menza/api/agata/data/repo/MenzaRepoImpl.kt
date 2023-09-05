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
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
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
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlin.time.Duration.Companion.days
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.lighthousegames.logging.logging

internal class MenzaSubsystemRepoImpl(
    private val api: CafeteriaApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
    hashStore: HashStore,
) : MenzaRepo {

    companion object {
        private val log = logging()
    }

    override val isReady: Flow<Boolean> =
        db.subsystemQueries.getAll()
            .asFlow()
            .mapToList()
            .map { it.isNotEmpty() }
            .distinctUntilChanged()
            .onEach { log.i { "Is ready: $it" } }

    override fun getData(): Flow<ImmutableList<Menza>> =
        db.subsystemQueries.getAll()
            .asFlow()
            .mapToList()
            .map { it.toDomain() }
            .distinctUntilChanged()
            .onEach { log.i { "Menza produced: ${it.size}" } }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

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
                log.d { "Stored menza list" }
            },
        )

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        checker.withCheckSince(ValidityKey.agataMenza(), isForced, 7.days) {
            processor.runSync(subsystemJob, db, isForced)
        }
    }
}

internal object MenzaStrahovRepoImpl : MenzaRepo {
    private val log = logging()

    override val isReady: Flow<Boolean> = MutableStateFlow(true)

    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        persistentListOf(
            Menza(
                type = Strahov,
                name = "Restaurace Strahov",
                isOpened = true,
                supportsDaily = true,
                supportsWeekly = false,
            ),
        ).let { emit(it) }
    }
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        SyncResult.Skipped.right()
    }
}
