/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.scraping

import cz.lastaapps.entity.menza.Contact
import cz.lastaapps.entity.menza.MenzaId
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class MenzaListScrapperTest {

    @ExperimentalCoroutinesApi
    @Test
    fun scrapeMenzaList() = runTest {

        val menzas = MenzaListScrapper.scrapeMenzaList().menzas

        menzas.forEach {
            println(it)
        }
        menzas.size shouldBe 11
        menzas.map { it.name } shouldContain "Menza Strahov"
        menzas.map { it.address.stringForm } shouldContain "Jezdecká 1920, 160 17 Praha 6"
        //menzas.find { it.name == "Technická menza" }?.opened shouldBe Opened.CLOSED
    }

    @ExperimentalCoroutinesApi
    @Test
    fun scrapContacts() = runTest {

        val contacts = MenzaListScrapper.scrapeMenzaList().contacts

        contacts.forEach {
            println(it)
        }

        contacts shouldContain Contact(
            MenzaId(1),
            "Vedoucí menzy",
            "",
            "+420234678291",
            "menza-strahov@cvut.cz"
        )

        contacts.size shouldBe 12
    }
}