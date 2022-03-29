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

import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.scraping.AllergensScraperImpl
import io.ktor.client.statement.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class AllergenQueryGenerator {
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun scrapeAllergens() = runTest {
        val request = AllergensScraperImpl.createRequestForAll()
        val data = AllergensScraperImpl.scrape(request.bodyAsText())
        insertAllergen(data)
    }

    private fun insertAllergen(data: Set<Allergen>) {
        data.forEach {
            QueryUtils.insert(
                "alergen",
                listOf("id_alergen", "jmeno", "cislo", "popis"),
                listOf(it.id.id, it.name, it.id.id, it.description),
            )
        }
        QueryUtils.resetTimer("alergen", "id_alergen")
    }
}