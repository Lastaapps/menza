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
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.storage.MemoryMenzaDriverFactory
import cz.lastaapps.storage.createMenzaDatabase
import cz.lastaapps.storage.repo.scrapers.AllergenScraperMock
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalCoroutinesApi
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AllergenRepoImplTest {

    private lateinit var db: MenzaDatabase

    @BeforeAll
    fun createDatabase() {
        db = createMenzaDatabase(MemoryMenzaDriverFactory())
    }

    @Test
    fun testRepo() = runTest {
        val allergens = setOf(
            Allergen(AllergenId(1), "1", "one"),
            Allergen(AllergenId(2), "2", "two"),
            Allergen(AllergenId(3), "3", "three"),
            Allergen(AllergenId(4), "4", "four"),
        )

        val repo = AllergenRepoImpl(db, AllergenScraperMock(allergens))

        val loaded = repo.getData(this).first()

        allergens shouldBe loaded.toSet()
    }
}