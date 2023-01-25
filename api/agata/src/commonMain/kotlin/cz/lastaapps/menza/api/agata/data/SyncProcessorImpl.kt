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

import arrow.core.continuations.Raise
import arrow.fx.coroutines.parMap
import com.squareup.sqldelight.Transacter
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.SyncProcessor
import cz.lastaapps.menza.api.agata.domain.model.SyncJob
import cz.lastaapps.menza.api.agata.domain.model.SyncJobHash
import cz.lastaapps.menza.api.agata.domain.model.SyncJobNoCache


// TODO rework with context receivers when usable in AS
internal class SyncProcessorImpl(
    private val hashStore: HashStore,
    private val database: Transacter,
) : SyncProcessor {

    override suspend fun run(list: Iterable<SyncJob<*>>) = outcome {
        list
            // Fetches hash codes from a remote source
            .parMap { job ->
                when (job) {
                    is SyncJobHash -> {
                        val hash = job.getHashCode().bind()

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

            // fetch data from api
            .parMap { (job, hash) ->
                val storeAction = processJob(job)
                Pair(storeAction, hash)
            }.let { results ->

                // store data in a single transaction
                database.transaction {
                    results.forEach { (action, _) -> action() }
                }

                // store new hash codes
                results.forEach { (_, hash) -> hash() }
            }
    }

    // used to simply generics resolution
    private suspend fun <T> Raise<MenzaError>.processJob(job: SyncJob<T>): () -> Unit {
        val res = job.fetchApi().bind()
        return { job.store(res) }
    }
}
