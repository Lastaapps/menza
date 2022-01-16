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

import android.util.Log
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneNotNull
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import cz.lastaapps.entity.allergens.Allergen
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.menza.MenzaLocation
import cz.lastaapps.menza.db.MenzaDatabase
import cz.lastaapps.scraping.AllergenScraper
import cz.lastaapps.scraping.ContactsScraper
import cz.lastaapps.scraping.LocationScraper
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.security.auth.login.LoginException
import kotlin.coroutines.CoroutineContext

class LocationRepoImpl<R : Any>(
    database: MenzaDatabase,
    private val scraper: LocationScraper<R>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LocationRepo {

    private val TAG = LocationRepo::class.simpleName
    private val queries = database.locationQueries

    override val errors: Channel<Errors>
        get() = mErrors
    override val requestInProgress: StateFlow<Boolean>
        get() = mRequestInProgress

    private val mErrors = Channel<Errors>(Channel.BUFFERED)
    private val mRequestInProgress = MutableStateFlow(false)

    override fun getData(scope: CoroutineScope): Flow<List<MenzaLocation>> {

        Log.i(TAG, "Getting data")
        scope.launch() {
            val hasData = hasDataStored().first()
            if (!hasData) {
                refreshInternal()
            }
        }

        return queries.getAll { menzaId, address, coordinates ->
            MenzaLocation(menzaId, address, coordinates)
        }
            .asFlow().mapToList(scope.coroutineContext)
    }

    override fun refreshData() : Flow<Boolean?> {
        return flow {
            Log.i(TAG, "Requesting data refresh")
            emit(refreshInternal())
        }
    }

    private suspend fun refreshInternal(): Boolean? {
        if (mRequestInProgress.value)
            return null

        mRequestInProgress.value = true

        val request = try {
            Log.i(TAG, "Getting data from a server")
            scraper.createRequest()
        } catch (e: Exception) {
            mErrors.send(Errors.ConnectionError)
            e.printStackTrace()
            mRequestInProgress.value = false
            return false
        }
        val data = try {
            Log.i(TAG, "Scraping")
            scraper.scrape(request)
        } catch (e: Exception) {
            mErrors.send(Errors.ParsingError)
            e.printStackTrace()
            mRequestInProgress.value = false
            return false
        }

        Log.i(TAG, "Replacing database entries")
        queries.transaction {
            queries.delete()
            data.forEach {
                queries.insert(it.id, it.address, it.location)
            }
        }
        mRequestInProgress.value = true
        return true
    }

    override fun hasDataStored(): Flow<Boolean> {
        Log.i(TAG, "Asking hasData")
        return queries.rowNumber().asFlow().mapToOneNotNull(dispatcher)
            .map {it > 0 }
    }
}