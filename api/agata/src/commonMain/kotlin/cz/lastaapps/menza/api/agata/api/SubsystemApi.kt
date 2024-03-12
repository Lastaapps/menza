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

package cz.lastaapps.menza.api.agata.api

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.extensions.catchingNetwork
import cz.lastaapps.menza.api.agata.data.AgataClient
import cz.lastaapps.menza.api.agata.data.model.ApiLang
import cz.lastaapps.menza.api.agata.data.model.Func.Address
import cz.lastaapps.menza.api.agata.data.model.Func.AddressHash
import cz.lastaapps.menza.api.agata.data.model.Func.Contacts
import cz.lastaapps.menza.api.agata.data.model.Func.ContactsHash
import cz.lastaapps.menza.api.agata.data.model.Func.Info
import cz.lastaapps.menza.api.agata.data.model.Func.InfoHash
import cz.lastaapps.menza.api.agata.data.model.Func.Link
import cz.lastaapps.menza.api.agata.data.model.Func.LinkHash
import cz.lastaapps.menza.api.agata.data.model.Func.News
import cz.lastaapps.menza.api.agata.data.model.Func.NewsHash
import cz.lastaapps.menza.api.agata.data.model.Func.Opening
import cz.lastaapps.menza.api.agata.data.model.Func.OpeningHash
import cz.lastaapps.menza.api.agata.data.model.dto.AddressDto
import cz.lastaapps.menza.api.agata.data.model.dto.ContactDto
import cz.lastaapps.menza.api.agata.data.model.dto.InfoDto
import cz.lastaapps.menza.api.agata.data.model.dto.LinkDto
import cz.lastaapps.menza.api.agata.data.model.dto.NewsDto
import cz.lastaapps.menza.api.agata.data.model.dto.OpenTimeDto
import cz.lastaapps.menza.api.agata.util.getFun
import io.ktor.client.call.body

internal sealed interface SubsystemApi {

    suspend fun getInfo(lang: ApiLang, subsystemId: Int): Outcome<List<InfoDto>?>
    suspend fun getInfoHash(lang: ApiLang, subsystemId: Int): Outcome<String>

    suspend fun getNews(lang: ApiLang, subsystemId: Int): Outcome<NewsDto?>
    suspend fun getNewsHash(lang: ApiLang, subsystemId: Int): Outcome<String>

    suspend fun getOpeningTimes(lang: ApiLang, subsystemId: Int): Outcome<List<OpenTimeDto>?>
    suspend fun getOpeningTimesHash(lang: ApiLang, subsystemId: Int): Outcome<String>

    suspend fun getContacts(lang: ApiLang): Outcome<List<ContactDto>?>
    suspend fun getContactsHash(lang: ApiLang): Outcome<String>

    suspend fun getAddress(lang: ApiLang): Outcome<List<AddressDto>?>
    suspend fun getAddressHash(lang: ApiLang): Outcome<String>

    suspend fun getLink(lang: ApiLang, subsystemId: Int): Outcome<List<LinkDto>?>
    suspend fun getLinkHash(lang: ApiLang, subsystemId: Int): Outcome<String>
}

internal class SubsystemApiImpl(
    agataClient: AgataClient,
) : SubsystemApi {
    private val client = agataClient.client

    override suspend fun getInfo(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<InfoDto>?> = catchingNetwork {
        client.getFun(Info, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getInfoHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(InfoHash, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getNews(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<NewsDto?> = catchingNetwork {
        client.getFun(News, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getNewsHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(NewsHash, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getOpeningTimes(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<OpenTimeDto>?> =
        catchingNetwork {
            client.getFun(Opening, lang, subsystemId = subsystemId).body()
        }

    override suspend fun getOpeningTimesHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(OpeningHash, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getContacts(
        lang: ApiLang,
    ): Outcome<List<ContactDto>?> = catchingNetwork {
        client.getFun(Contacts, lang).body()
    }

    override suspend fun getContactsHash(
        lang: ApiLang,
    ): Outcome<String> = catchingNetwork {
        client.getFun(ContactsHash, lang).body()
    }

    override suspend fun getAddress(
        lang: ApiLang,
    ): Outcome<List<AddressDto>?> = catchingNetwork {
        client.getFun(Address, lang).body()
    }

    override suspend fun getAddressHash(
        lang: ApiLang,
    ): Outcome<String> = catchingNetwork {
        client.getFun(AddressHash, lang).body()
    }

    override suspend fun getLink(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<List<LinkDto>?> = catchingNetwork {
        client.getFun(Link, lang, subsystemId = subsystemId).body()
    }

    override suspend fun getLinkHash(
        lang: ApiLang,
        subsystemId: Int,
    ): Outcome<String> = catchingNetwork {
        client.getFun(LinkHash, lang, subsystemId = subsystemId).body()
    }
}
