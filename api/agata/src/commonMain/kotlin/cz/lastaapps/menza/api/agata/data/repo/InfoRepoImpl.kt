/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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
import app.cash.sqldelight.coroutines.mapToOneOrNull
import arrow.core.rightIor
import co.touchlab.kermit.Logger
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.Info
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.repo.InfoRepoParams
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.api.core.domain.validity.withParams
import cz.lastaapps.core.util.extensions.combine6
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.data.mapers.toDomain
import cz.lastaapps.menza.api.agata.data.mapers.toEntity
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.data.model.toDB
import cz.lastaapps.menza.api.agata.data.model.toDto
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.days

internal class InfoRepoImpl(
    private val subsystemId: Int,
    private val subsystemApi: SubsystemApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor<InfoRepoParams>,
    private val checker: ValidityChecker,
    hashStore: HashStore,
) : InfoRepo {
    private val log = Logger.withTag(this::class.simpleName + "($subsystemId)")
    private val validityKey = ValidityKey.agataInfo(subsystemId)

    override fun getData(params: InfoRepoParams): Flow<Info> =
        run {
            val lang = params.language.toDB()
            combine6(
                db.infoQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToOneOrNull(Dispatchers.IO),
                db.newsQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToOneOrNull(Dispatchers.IO),
                db.contactQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToList(Dispatchers.IO),
                db.openTimeQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToList(Dispatchers.IO),
                db.linkQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToList(Dispatchers.IO),
                db.addressQueries
                    .getForSubsystem(subsystemId.toLong(), lang)
                    .asFlow()
                    .mapToOneOrNull(Dispatchers.IO),
            ) { info, news, contacts, openTimes, links, address ->
                info.toDomain(news, contacts, openTimes, links, address)
            }.onStart { log.i { "Starting collection" } }
                .onCompletion { log.i { "Completed collection" } }
        }

    private val jobs =
        listOf<SyncJobHash<*, *, InfoRepoParams>>(
            // Info
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.infoHash(subsystemId).withLang(it.language) },
                getHashCode = { subsystemApi.getInfoHash(it.language.toDto(), subsystemId).bind() },
                fetchApi = {
                    subsystemApi.getInfo(it.language.toDto(), subsystemId).bind().orEmpty()
                },
                convert = { params, data -> data.map { it.toEntity(params.language) }.rightIor() },
                store = { params, data ->
                    db.infoQueries.deleteSubsystem(params.language.toDB(), subsystemId.toLong())
                    data.forEach {
                        db.infoQueries.insert(it)
                    }
                    log.d { "Info stored" }
                },
            ),
            // News
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.newsHash(subsystemId).withLang(it.language) },
                getHashCode = { subsystemApi.getNewsHash(it.language.toDto(), subsystemId).bind() },
                fetchApi = { subsystemApi.getNews(it.language.toDto(), subsystemId).bind() },
                convert = { params, data ->
                    data?.toEntity(subsystemId, params.language).rightIor()
                },
                store = { params, data ->
                    db.newsQueries.deleteForSubsystem(params.language.toDB(), subsystemId.toLong())
                    data?.let {
                        db.newsQueries.insert(data)
                    }
                    log.d { "News stored" }
                },
            ),
            // Contacts
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.contactsHash().withLang(it.language) },
                getHashCode = { subsystemApi.getContactsHash(it.language.toDto()).bind() },
                fetchApi = { subsystemApi.getContacts(it.language.toDto()).bind().orEmpty() },
                convert = { params, data -> data.map { it.toEntity(params.language) }.rightIor() },
                store = { params, data ->
                    db.contactQueries.deleteAll(params.language.toDB())
                    data.forEach {
                        db.contactQueries.insert(it)
                    }
                    log.d { "Contacts stored" }
                },
            ),
            // OpenTimes
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.openingHash(subsystemId).withLang(it.language) },
                getHashCode = {
                    subsystemApi.getOpeningTimesHash(it.language.toDto(), subsystemId).bind()
                },
                fetchApi = {
                    subsystemApi.getOpeningTimes(it.language.toDto(), subsystemId).bind().orEmpty()
                },
                convert = { params, data -> data.map { it.toEntity(params.language) }.rightIor() },
                store = { params, data ->
                    db.openTimeQueries.deleteSubsystem(params.language.toDB(), subsystemId.toLong())
                    data.forEach {
                        db.openTimeQueries.insert(it)
                    }
                    log.d { "Open times stored" }
                },
            ),
            // Links
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.linkHash(subsystemId).withLang(it.language) },
                getHashCode = { subsystemApi.getLinkHash(it.language.toDto(), subsystemId).bind() },
                fetchApi = {
                    subsystemApi.getLink(it.language.toDto(), subsystemId).bind().orEmpty()
                },
                convert = { params, data -> data.map { it.toEntity(params.language) }.rightIor() },
                store = { params, data ->
                    db.linkQueries.deleteSubsystem(params.language.toDB(), subsystemId.toLong())
                    data.forEach {
                        db.linkQueries.insert(it)
                    }
                    log.d { "Links stored" }
                },
            ),
            // Address
            SyncJobHash(
                hashStore = hashStore,
                hashType = { HashType.addressHash().withLang(it.language) },
                getHashCode = { subsystemApi.getAddressHash(it.language.toDto()).bind() },
                fetchApi = { subsystemApi.getAddress(it.language.toDto()).bind().orEmpty() },
                convert = { params, data -> data.map { it.toEntity(params.language) }.rightIor() },
                store = { params, data ->
                    db.addressQueries.deleteAll(params.language.toDB())
                    data.forEach {
                        db.addressQueries.insert(it)
                    }
                    log.d { "Address stored" }
                },
            ),
        )

    override suspend fun sync(
        params: InfoRepoParams,
        isForced: Boolean,
    ): SyncOutcome =
        run {
            log.i { "Starting sync (f: $isForced)" }
            checker.withCheckSince(validityKey.withParams(params), isForced, 7.days) {
                processor.runSync(jobs, db, params, isForced)
            }
        }
}

internal object InfoStrahovRepoImpl : InfoRepo, KoinComponent {
    private val strahovInfoRepo: InfoRepo by
        inject<InfoRepo> { parametersOf(MenzaType.Agata.Subsystem(1)) }

    override fun getData(params: InfoRepoParams): Flow<Info> = strahovInfoRepo.getData(params)

    override suspend fun sync(
        params: InfoRepoParams,
        isForced: Boolean,
    ): SyncOutcome = strahovInfoRepo.sync(params, isForced = isForced)
}
