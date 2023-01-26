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

import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.IorNel
import arrow.core.toNonEmptyListOrNull
import arrow.fx.coroutines.parMap
import com.squareup.sqldelight.Transacter
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncJobHash
import cz.lastaapps.api.core.domain.sync.SyncJobNoCache
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlinx.collections.immutable.persistentListOf


// TODO rework with context receivers when usable in AS
internal class SyncProcessorImpl(
    private val hashStore: HashStore,
    private val database: Transacter,
) : SyncProcessor {

    override suspend fun run(list: Iterable<SyncJob<*, *>>): SyncOutcome = outcome {
        list
            // Fetches hash codes from a remote source
            .parMap { job ->
                when (job) {
                    is SyncJobHash -> {
                        val hash = job.getHashCode()

                        if (hashStore.shouldReload(job.hashType, hash)) {
                            // deferred job to save the new hash code
                            val storeHashAction: suspend () -> Unit =
                                { hashStore.storeHash(job.hashType, hash) }

                            // process this job
                            Pair(job, storeHashAction)
                        } else {
                            // skip this job
                            null
                        }
                    }
                    is SyncJobNoCache -> {
                        val noOp: suspend () -> Unit = {}
                        Pair(job, noOp)
                    }
                }
            }
            // remove skipped jobs
            .filterNotNull()

            // Fetch data from api and convert then
            .parMap { (job, hash) ->
                val storeAction = job.processFetchAndConvert().bind()
                Pair(storeAction, hash)
            }

            // Store data
            .also { results ->

                // store data in a single transaction
                database.transaction {
                    results.forEach { (action, _) -> action.map { it() } }
                }

                // store new hash codes
                results.forEach { (_, hash) -> hash() }
            }

            // collect noncritical errors
            .map(Pair<IorNel<MenzaError, *>, *>::first)
            .foldRight(persistentListOf<MenzaError>()) { item, acu ->
                acu.addAll(
                    // defeated male leaves
                    when (item) {
                        is Both -> item.leftValue
                        is Left -> item.value
                        is Right -> emptyList()
                    }
                )
            }.let {
                it.toNonEmptyListOrNull()?.let { nel ->
                    SyncResult.Problem(nel)
                } ?: SyncResult.Updated
            }
    }

    // used to simply generics resolution
    private suspend fun <T, R> SyncJob<T, R>.processFetchAndConvert(): Outcome<IorNel<MenzaError, () -> Unit>> =
        outcome {
            val fetched = fetchApi()

            convert(fetched).map { data ->
                { store(data) }
            }
        }
}
