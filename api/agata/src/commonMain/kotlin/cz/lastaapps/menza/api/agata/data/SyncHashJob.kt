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

import arrow.core.IorNel
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.squareup.sqldelight.Transacter
import cz.lastaapps.api.core.domain.model.HashType
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.MenzaRaise
import cz.lastaapps.menza.api.agata.domain.HashStore

/**
 * Job info for a sync processor using hash
 */
// TODO consider adding shouldRun condition and moving this to the Agata module
internal class SyncJobHash<T, R>(
    private val hashStore: HashStore,
    private val hashType: HashType,
    private val getHashCode: suspend MenzaRaise.() -> String,
    override val fetchApi: suspend MenzaRaise.() -> T,
    override val convert: suspend MenzaRaise.(T) -> IorNel<MenzaError, R>,
    override val store: (R) -> Unit,
) : SyncJob<T, R> {
    override val shouldRun: suspend MenzaRaise.() -> Option<suspend () -> Unit> = {
        val hash = getHashCode()

        if (hashStore.shouldReload(hashType, hash)) {
            // deferred job to save the new hash code
            val storeHashAction: suspend () -> Unit =
                { hashStore.storeHash(hashType, hash) }

            // process this job
            Some(storeHashAction)
        } else {
            // skip this job
            None
        }
    }
}

internal suspend fun <T, R> SyncProcessor.runSync(job: SyncJob<T, R>, db: Transacter) =
    runSync(listOf(job), db)

internal suspend fun SyncProcessor.runSync(
    list: Iterable<SyncJob<*, *>>,
    db: Transacter,
): SyncOutcome =
    runSync(list, listOf { db.transaction { it() } })
