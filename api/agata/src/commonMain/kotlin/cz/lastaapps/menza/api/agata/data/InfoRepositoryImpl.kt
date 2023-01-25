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

package cz.lastaapps.menza.api.agata.data

import arrow.core.right
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.combine6
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.domain.SyncProcessor
import cz.lastaapps.menza.api.agata.domain.model.HashType
import cz.lastaapps.menza.api.agata.domain.model.InfoRepository
import cz.lastaapps.menza.api.agata.domain.model.SyncJobHash
import cz.lastaapps.menza.api.agata.domain.model.SyncJobNoCache
import cz.lastaapps.menza.api.agata.domain.model.common.Info
import cz.lastaapps.menza.api.agata.domain.model.common.NewsHeader
import cz.lastaapps.menza.api.agata.domain.model.mapers.toDomain
import cz.lastaapps.menza.api.agata.domain.model.mapers.toEntity
import cz.lastaapps.menza.api.agata.domain.model.mapers.toNews
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow

internal class InfoRepositoryImpl(
    private val subsystemId: Int,
    private val subsystemApi: SubsystemApi,
    private val db: AgataDatabase,
    private val processor: SyncProcessor,
) : InfoRepository {

    private val newsFlow = MutableStateFlow<NewsHeader?>(null)

    override fun getData(): Flow<Info> =
        combine6(
            db.infoQueries.getForSubsystem(subsystemId.toLong()).asFlow().mapToOneOrNull(),
            newsFlow,
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
            hashType = HashType.infoHash(subsystemId),
            getHashCode = { subsystemApi.getInfoHash(subsystemId) },
            fetchApi = { subsystemApi.getInfo(subsystemId) },
            store = { data ->
                db.infoQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.infoQueries.insertEntity(it.toEntity())
                }
            },
        ),
        // News
        SyncJobNoCache(
            fetchApi = { subsystemApi.getNews(subsystemId) },
            store = { data ->
                newsFlow.value = data.toNews()
            },
        ),
        // Contacts
        SyncJobHash(
            hashType = HashType.contactsHash(),
            getHashCode = { subsystemApi.getContactsHash() },
            fetchApi = { subsystemApi.getContacts() },
            store = { data ->
                db.contactQueries.deleteAll()
                data.forEach {
                    db.contactQueries.insertEntity(it.toEntity())
                }
            },
        ),
        // OpenTimes
        SyncJobHash(
            hashType = HashType.openingHash(subsystemId),
            getHashCode = { subsystemApi.getOpeningTimesHash(subsystemId) },
            fetchApi = { subsystemApi.getOpeningTimes(subsystemId) },
            store = { data ->
                db.openTimeQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.openTimeQueries.insertEntity(it.toEntity())
                }
            },
        ),
        // Links
        SyncJobHash(
            hashType = HashType.linkHash(subsystemId),
            getHashCode = { subsystemApi.getLinkHash(subsystemId) },
            fetchApi = { subsystemApi.getLink(subsystemId) },
            store = { data ->
                db.linkQueries.deleteSubsystem(subsystemId.toLong())
                data.forEach {
                    db.linkQueries.insertEntity(it.toEntity())
                }
            },
        ),
        // Address
        SyncJobHash(
            hashType = HashType.addressHash(),
            getHashCode = { subsystemApi.getAddressHash() },
            fetchApi = { subsystemApi.getAddress() },
            store = { data ->
                db.addressQueries.deleteAll()
                data.forEach {
                    db.addressQueries.insertEntity(it.toEntity())
                }
            },
        ),
    )

    override suspend fun sync(): Outcome<Unit> =
        processor.run(jobs)
}

internal object InfoRepositoryStrahovImpl : InfoRepository {
    private val flow = flow { emit(Info.empty) }

    override fun getData(): Flow<Info> = flow
    override suspend fun sync(): Outcome<Unit> = Unit.right()
}
