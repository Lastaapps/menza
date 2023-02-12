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
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.api.core.domain.model.common.Info
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckSince
import cz.lastaapps.core.util.extensions.combine6
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.data.SyncJobHash
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.HashType
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.model.mapers.toEntity
import kotlin.time.Duration.Companion.days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class InfoRepoImpl(
    private val subsystemId: Int,
    private val subsystemApi: SubsystemApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
    hashStore: HashStore,
) : InfoRepo {

    private val hasBeenSynced = MutableStateFlow(false)

    override fun getData(): Flow<Info> =
        combine6(
            db.infoQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToOneOrNull(),
            db.newsQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToOneOrNull(),
            db.contactQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToList(),
            db.openTimeQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToList(),
            db.linkQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToList(),
            db.addressQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToOneOrNull(),
        ) { info, news, contacts, openTimes, links, address ->
            info.toDomain(news, contacts, openTimes, links, address)
        }

    private val jobs = listOf(
        // Info
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.infoHash(subsystemId),
            getHashCode = { subsystemApi.getInfoHash(subsystemId).bind() },
            fetchApi = { subsystemApi.getInfo(subsystemId).bind().orEmpty() },
            convert = { data -> data.map { it.toEntity() }.rightIor() },
            store = { data ->
                db.infoQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.infoQueries.insert(it)
                }
            },
        ),
        // News
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.newsHash(subsystemId),
            getHashCode = { subsystemApi.getNewsHash(subsystemId).bind() },
            fetchApi = { subsystemApi.getNews(subsystemId).bind() },
            convert = { data -> data?.toEntity(subsystemId).rightIor() },
            store = { data ->
                db.newsQueries.deleteForSubsystem(subsystemId.toLong())
                data?.let {
                    db.newsQueries.insert(data)
                }
            },
        ),
        // Contacts
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.contactsHash(),
            getHashCode = { subsystemApi.getContactsHash().bind() },
            fetchApi = { subsystemApi.getContacts().bind().orEmpty() },
            convert = { data -> data.map { it.toEntity() }.rightIor() },
            store = { data ->
                db.contactQueries.deleteAll()
                data.forEach {
                    db.contactQueries.insert(it)
                }
            },
        ),
        // OpenTimes
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.openingHash(subsystemId),
            getHashCode = { subsystemApi.getOpeningTimesHash(subsystemId).bind() },
            fetchApi = { subsystemApi.getOpeningTimes(subsystemId).bind().orEmpty() },
            convert = { data -> data.map { it.toEntity() }.rightIor() },
            store = { data ->
                db.openTimeQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.openTimeQueries.insert(it)
                }
            },
        ),
        // Links
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.linkHash(subsystemId),
            getHashCode = { subsystemApi.getLinkHash(subsystemId).bind() },
            fetchApi = { subsystemApi.getLink(subsystemId).bind().orEmpty() },
            convert = { data -> data.map { it.toEntity() }.rightIor() },
            store = { data ->
                db.linkQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.linkQueries.insert(it)
                }
            },
        ),
        // Address
        SyncJobHash(
            hashStore = hashStore,
            hashType = HashType.addressHash(),
            getHashCode = { subsystemApi.getAddressHash().bind() },
            fetchApi = { subsystemApi.getAddress().bind().orEmpty() },
            convert = { data -> data.map { it.toEntity() }.rightIor() },
            store = { data ->
                db.addressQueries.deleteAll()
                data.forEach {
                    db.addressQueries.insert(it)
                }
            },
        ),
    )

    override suspend fun sync(isForced: Boolean): SyncOutcome {
        val localForce = isForced || !hasBeenSynced.value
        return checker.withCheckSince(ValidityKey.agataInfo(subsystemId), localForce, 7.days) {
            processor.runSync(jobs, db, localForce)
        }.onRight {
            hasBeenSynced.value = true
        }
    }
}

internal object InfoStrahovRepoImpl : InfoRepo {
    override fun getData(): Flow<Info> = flow { emit(Info.empty) }
    override suspend fun sync(isForced: Boolean): SyncOutcome = SyncResult.Unavailable.right()
}
