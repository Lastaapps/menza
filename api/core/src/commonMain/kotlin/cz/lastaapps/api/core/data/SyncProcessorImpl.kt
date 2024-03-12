/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.core.util.extensions.withTimeoutOutcome
import kotlin.time.Duration.Companion.seconds
import kotlinx.collections.immutable.persistentListOf

internal class SyncProcessorImpl<Params> : SyncProcessor<Params> {

    companion object {
        // to prevent unnecessary timeouts because of slow connection or whatever
        private const val CONCURRENCY = 4

        // if you don't make it in time, you are doing something wrong
        private val jobTimeout = 8.seconds
    }

    private val log = localLogger()

    override suspend fun runSync(
        list: Iterable<SyncJob<*, *, Params>>,
        scope: List<(() -> Unit) -> Unit>,
        params: Params,
        isForced: Boolean,
    ): SyncOutcome = outcome {
        list.also { log.i { "Preparing run conditions" } }
            // Fetches hash codes from a remote source
            .parMap(concurrency = CONCURRENCY) { job ->
                withTimeoutOutcome(jobTimeout) {
                    job.shouldRun(this@outcome, params, isForced).map { job to it }
                }.bind()
            }
            // remove skipped jobs
            .filterIsInstance<Some<Pair<SyncJob<*, *, Params>, suspend () -> Unit>>>()
            .map { it.value }
            .also { log.i { "Executing" } }
            // Fetch data from api and convert then
            .parMap(concurrency = CONCURRENCY) { (job, hash) ->
                withTimeoutOutcome(jobTimeout) {
                    val storeAction = job.processFetchAndConvert(params).bind()
                    Pair(storeAction, hash)
                }.bind()
            }
            // Store data
            .also { log.i { "Storing data" } }
            .also { results ->

                // store data in a single transaction
                scope.foldRight(
                    { results.forEach { (action, _) -> action.map { it() } } },
                ) { func, acu ->
                    { func(acu) }
                }.invoke()

                // store new hash codes
                results.forEach { (_, hash) -> hash() }
            }
            // collect noncritical errors
            .map(Pair<IorNel<DomainError, *>, *>::first)
            .foldRight(persistentListOf<DomainError>()) { item, acu ->
                acu.addAll(
                    // defeated male leaves
                    when (item) {
                        is Both -> item.leftValue
                        is Left -> item.value
                        is Right -> emptyList()
                    },
                )
            }.let {
                it.toNonEmptyListOrNull()?.let { nel ->
                    SyncResult.Problem(nel)
                } ?: SyncResult.Updated
            }
    }
        .onRight { log.i { "Succeed to process data: $it" } }
        .onLeft { log.i { "Failed to process data ${it::class.simpleName}" } }

    // used to simply generics resolution
    private suspend fun <T, R> SyncJob<T, R, Params>.processFetchAndConvert(params: Params): Outcome<IorNel<DomainError, () -> Unit>> =
        outcome {
            val fetched = fetchApi(params)

            convert(params, fetched).map { data ->
                { store(params, data) }
            }
        }
}
