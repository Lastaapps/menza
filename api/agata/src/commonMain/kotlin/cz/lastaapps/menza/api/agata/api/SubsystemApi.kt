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

package cz.lastaapps.menza.api.agata.api

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.catchingNetwork
import cz.lastaapps.menza.api.agata.domain.model.Func.Contacts
import cz.lastaapps.menza.api.agata.domain.model.Func.ContactsHash
import cz.lastaapps.menza.api.agata.domain.model.Func.Info
import cz.lastaapps.menza.api.agata.domain.model.Func.InfoHash
import cz.lastaapps.menza.api.agata.domain.model.Func.Link
import cz.lastaapps.menza.api.agata.domain.model.Func.LinkHash
import cz.lastaapps.menza.api.agata.domain.model.Func.News
import cz.lastaapps.menza.api.agata.domain.model.Func.Opening
import cz.lastaapps.menza.api.agata.domain.model.Func.OpeningHash
import cz.lastaapps.menza.api.agata.domain.model.dto.ContactDto
import cz.lastaapps.menza.api.agata.domain.model.dto.InfoDto
import cz.lastaapps.menza.api.agata.domain.model.dto.LinkDto
import cz.lastaapps.menza.api.agata.domain.model.dto.OpenTimeDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.HttpClient
import io.ktor.client.call.body

internal sealed interface SubsystemApi {

    suspend fun getInfo(subsystemId: Int): Outcome<List<InfoDto>>
    suspend fun getInfoHash(subsystemId: Int): Outcome<String>

    suspend fun getNews(subsystemId: Int): Outcome<List<String>>

    suspend fun getOpeningTimes(subsystemId: Int): Outcome<List<OpenTimeDto>>
    suspend fun getOpeningTimesHash(subsystemId: Int): Outcome<String>

    suspend fun getContacts(): Outcome<List<ContactDto>>
    suspend fun getContactsHash(): Outcome<String>

    suspend fun getLink(subsystemId: Int): Outcome<List<LinkDto>>
    suspend fun getLinkHash(subsystemId: Int): Outcome<String>

}

internal class SubsystemApiImpl(
    private val client: HttpClient,
) : SubsystemApi {

    override suspend fun getInfo(subsystemId: Int): Outcome<List<InfoDto>> = catchingNetwork {
        client.getFun(Info, subsystemId = subsystemId).body()
    }

    override suspend fun getInfoHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(InfoHash, subsystemId = subsystemId).body()
    }

    override suspend fun getNews(subsystemId: Int): Outcome<List<String>> = catchingNetwork {
        client.getFun(News, subsystemId = subsystemId).body()
    }

    override suspend fun getOpeningTimes(subsystemId: Int): Outcome<List<OpenTimeDto>> =
        catchingNetwork {
            client.getFun(Opening, subsystemId = subsystemId).body()
        }

    override suspend fun getOpeningTimesHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(OpeningHash, subsystemId = subsystemId).body()
    }

    override suspend fun getContacts(): Outcome<List<ContactDto>> = catchingNetwork {
        client.getFun(Contacts).body()
    }

    override suspend fun getContactsHash(): Outcome<String> = catchingNetwork {
        client.getFun(ContactsHash).body()
    }

    override suspend fun getLink(subsystemId: Int): Outcome<List<LinkDto>> = catchingNetwork {
        client.getFun(Link).body()
    }

    override suspend fun getLinkHash(subsystemId: Int): Outcome<String> = catchingNetwork {
        client.getFun(LinkHash).body()
    }

}
