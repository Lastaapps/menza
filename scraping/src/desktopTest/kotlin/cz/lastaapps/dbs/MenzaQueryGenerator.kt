/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.dbs

import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.info.OpeningHours
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaLocation
import cz.lastaapps.entity.menza.Message
import cz.lastaapps.entity.menza.Opened
import cz.lastaapps.scraping.*
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MenzaQueryGenerator {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scrapeMenzaInfo() = runTest() {
        val menzaList = MenzaScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }
        val opening = OpeningHoursScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }
        val contacts = ContactsScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }
        val location = LocationScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }
        val message = MessagesScraperImpl.run {
            scrape(createRequest().bodyAsText())
        }

        insertMenzas(menzaList, location, message)
        insertContacts(contacts)
        insertOpening(opening)
    }

    private fun insertMenzas(
        menzaList: Set<Menza>, locations: Set<MenzaLocation>, messages: Set<Message>
    ) {
        menzaList.forEach { menza ->
            val location = locations.first { it.id == menza.menzaId }
            val message = messages.firstOrNull { it.id == menza.menzaId }
            QueryUtils.insert(
                "menza",
                listOf(
                    "id_menza", "jmeno", "v_provozu",
                    "adresa", "zem_sirka", "zem_delka",
                    "zprava",
                ),
                listOf(
                    menza.menzaId.id,
                    menza.name,
                    menza.opened == Opened.OPENED,
                    location.address.stringForm,
                    location.coordinates.long,
                    location.coordinates.lat,
                    message?.message,
                ),
            )
        }
        QueryUtils.resetTimer("menza", "id_menza")
        println()
    }

    private fun insertContacts(contacts: Set<Contact>) {
        contacts.forEach {
            QueryUtils.insert(
                "kontakt",
                listOf("id_menza", "oznaceni", "telefon", "email"),
                listOf(
                    it.id.id,
                    it.name?.name ?: it.role?.role,
                    it.phoneNumber?.phone,
                    it.email?.mail
                )
            )
        }
        println()
    }

    private fun insertOpening(opening: Set<OpeningHours>) {
        val grouped = opening.groupBy { it.menzaId to it.locationName }
        var sectionId = 1
        grouped.entries.forEach { pair ->
            val currentId = sectionId++
            QueryUtils.insert(
                "sektor",
                listOf("id_sektor", "id_menza", "jmeno"),
                listOf(currentId, pair.key.first.id, pair.key.second),
            )
            pair.value.forEach {
                QueryUtils.insert(
                    "oteviraci_doba",
                    listOf("den_tydnu", "id_sektor", "od_cas", "do_cas"),
                    listOf(it.dayOfWeek, currentId, it.open, it.close)
                )
            }
        }
        QueryUtils.resetTimer("sektor", "id_sektor")
        println()
    }
}