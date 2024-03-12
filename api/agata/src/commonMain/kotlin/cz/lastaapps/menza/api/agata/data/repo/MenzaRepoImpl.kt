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

import agata.SubsystemEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import arrow.core.right
import arrow.core.rightIor
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.RequestLanguage.CS
import cz.lastaapps.api.core.domain.model.RequestLanguage.EN
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.repo.MenzaRepoParams
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.data.model.dto.SubsystemDto
import cz.lastaapps.menza.api.agata.data.model.toDB
import cz.lastaapps.menza.api.agata.data.model.toDto
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlin.time.Duration.Companion.days
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart


internal class MenzaSubsystemRepoImpl(
    private val api: CafeteriaApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor<MenzaRepoParams>,
    private val checker: ValidityChecker,
    hashStore: HashStore,
) : MenzaRepo {

    private val log = localLogger()

    override fun isReady(params: MenzaRepoParams): Flow<Boolean> =
        db.subsystemQueries.getAll(params.language.toDB())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.isNotEmpty() }
            .distinctUntilChanged()
            .onEach { log.i { "Is ready: $it" } }

    override fun getData(params: MenzaRepoParams): Flow<ImmutableList<Menza>> =
        db.subsystemQueries.getAll(params.language.toDB())
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.toDomain() }
            .map { it.provideVideoLinks().toImmutableList() }
            .distinctUntilChanged()
            .onEach { log.i { "Menza produced: ${it.size}" } }
            .onStart { log.i { "Starting collection" } }
            .onCompletion { log.i { "Completed collection" } }

    private val subsystemJob =
        SyncJobHash<List<SubsystemDto>, List<SubsystemEntity>, MenzaRepoParams>(
            hashStore = hashStore,
            hashType = { HashType.subsystemHash().withLang(it.language) },
            getHashCode = { api.getSubsystemsHash(it.language.toDto()).bind() },
            fetchApi = {
                api.getSubsystems(it.language.toDto()).bind().orEmpty()
            },
            convert = { params, dtos ->
                dtos.map { dto -> dto.toEntity(params.language) }.rightIor()
            },
            store = { _, result ->
                db.subsystemQueries.deleteAll()
                result.forEach { dto ->
                    db.subsystemQueries.insert(dto)
                }
                log.d { "Stored menza list" }
            },
        )

    private fun Collection<Menza>.provideVideoLinks() = map {
        when (it.type) {
            MenzaType.Agata.Subsystem(2) -> it.copy(
                videoLinks = persistentListOf(
                    "https://agata.suz.cvut.cz/jidelnicky/sd-cam-img.php",
                ),
            )

            else -> it
        }
    }

    override suspend fun sync(params: MenzaRepoParams, isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        checker.withCheckSince(ValidityKey.agataMenza(), isForced, 7.days) {
            processor.runSync(subsystemJob, db, params, isForced)
        }
    }
}

internal object MenzaStrahovRepoImpl : MenzaRepo {
    private val log = localLogger()

    override fun isReady(params: MenzaRepoParams): Flow<Boolean> = MutableStateFlow(true)

    override fun getData(params: MenzaRepoParams): Flow<ImmutableList<Menza>> = flow {
        persistentListOf(
            Menza(
                type = Strahov,
                name = getName(params),
                isOpened = true,
                supportsDaily = true,
                supportsWeekly = false,
                isExperimental = false,
                videoLinks = persistentListOf(),
            ),
        ).let { emit(it) }
    }
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    private fun getName(params: MenzaRepoParams) = when (params.language) {
        CS -> "Restaurace Strahov"
        EN -> "Restaurant Strahov"
    }

    override suspend fun sync(params: MenzaRepoParams, isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        SyncResult.Skipped.right()
    }
}
