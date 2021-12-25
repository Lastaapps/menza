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

package cz.lastaapps.storage.repo

import com.squareup.sqldelight.runtime.coroutines.asFlow
import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.AllergenScraper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AllergenRepoImpl<R : Any>(
    private val database: MenzaDatabase,
    private val allergenScraper: AllergenScraper<R>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AllergenRepo {
    override val hasData: StateFlow<Boolean>
        get() = mHasData
    override val isLoading: StateFlow<Boolean>
        get() = mIsLoading
    override val failed: StateFlow<Boolean>
        get() = mFailed

    private val mHasData = MutableStateFlow(false)
    private val mIsLoading = MutableStateFlow(false)
    private val mFailed = MutableStateFlow(false)

    //TODO error channel
    private val errorChannel = Channel<String>()

    override suspend fun getData(): Flow<List<Allergen>> = withContext(dispatcher) {

        val queries = database.allergenQueries
        val exists = queries.hasData().executeAsOneOrNull() != null

        if (!exists) {
            loadFromWeb()
        }

        return@withContext queries.getAllAlergens()
            .asFlow().map { it.executeAsList() }
            .map { list -> list.map { Allergen(it.id, it.name, it.description) }.sorted() }
    }

    suspend fun reload() {
        loadFromWeb()
    }

    private suspend fun loadFromWeb() {
        val request = allergenScraper.createRequestForAll()
        val data = allergenScraper.scrape(request)

        val queries = database.allergenQueries
        queries.transaction {
            queries.deleteAllergens()
            data.forEach {
                queries.insertAllergens(it.id, it.name, it.description)
            }
        }
    }
}