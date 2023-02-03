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

package cz.lastaapps.api.core.data

import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.IorNel
import arrow.core.Some
import arrow.core.toNonEmptyListOrNull
import arrow.fx.coroutines.parMap
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.outcome
import kotlinx.collections.immutable.persistentListOf


internal class SyncProcessorImpl : SyncProcessor {

    override suspend fun runSync(
        list: Iterable<SyncJob<*, *>>,
        scope: List<(() -> Unit) -> Unit>,
        isForced: Boolean,
    ): SyncOutcome = outcome {
        list
            // Fetches hash codes from a remote source
            .parMap { job ->
                job.shouldRun(this@outcome, isForced).map { job to it }
            }

            // remove skipped jobs
            .filterIsInstance<Some<Pair<SyncJob<*, *>, suspend () -> Unit>>>()
            .map { it.value }

            // Fetch data from api and convert then
            .parMap { (job, hash) ->
                val storeAction = job.processFetchAndConvert().bind()
                Pair(storeAction, hash)
            }

            // Store data
            .also { results ->

                // store data in a single transaction
                scope.foldRight(
                    { results.forEach { (action, _) -> action.map { it() } } }
                ) { func, acu ->
                    { func(acu) }
                }.invoke()

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
