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

package cz.lastaapps.storage.repo

import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.info.*
import cz.lastaapps.entity.menza.*
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.storage.repo.scrapers.*
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldNotBeEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.lighthousegames.logging.logging
import java.time.DayOfWeek

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeneralStorageRepoTest {

    private lateinit var db: MenzaDatabase

    @BeforeAll
    fun createDatabase() {
        db = createMenzaDatabase(MemoryMenzaDriverFactory())
    }

    @Test
    fun testHasData() = runTest {
        val repos = listOf(
            AllergenRepoImpl(db, AllergenScraperMock(setOf(Allergen(AllergenId(1), "1", "one")))),
            ContactsRepoImpl(
                db, ContactsScraperMock(
                    setOf(
                        Contact(
                            MenzaId(1), Name("name"), Role("role"),
                            PhoneNumber("123456789"), Email("hans@example.com")
                        )
                    )
                )
            ),
            LocationRepoImpl(
                db, LocationScraperMock(
                    setOf(
                        MenzaLocation(
                            MenzaId(1), Address(("abc")), Coordinates("50", "15")
                        )
                    )
                )
            ),
            MenzaRepoImpl(db, MenzaScraperMock(setOf(Menza(MenzaId(1), "Menza", Opened.OPENED)))),
            MessagesRepoImpl(db, MessagesScraperMock(setOf(Message(MenzaId(1), "text")))),
            OpeningHoursRepoImpl(
                db, OpeningHoursMock(
                    setOf(
                        OpeningHours(
                            MenzaId(1), "name", DayOfWeek.MONDAY,
                            LocalTime(0, 0, 0), LocalTime(0, 0, 0), "comment"
                        )
                    )
                )
            ),
        )

        //non TestScore required, so delay() aren't skipped
        withContext(Dispatchers.Default) {
            repos.forEach { repo ->
                logging().i { "Testing ${repo::class.simpleName}" }
                repo.hasData().shouldBeFalse()
                repo.getData(this).first().shouldBeEmpty()

                delay(100)
                repo.getData(this).first().shouldNotBeEmpty()
                repo.hasData().shouldBeTrue()

                repo.refreshData()
                delay(100)
                repo.getData(this).first().shouldNotBeEmpty()
                repo.hasData().shouldBeTrue()
            }
        }
    }
}