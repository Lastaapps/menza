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
import arrow.core.Some
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.MenzaRaise
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.HashType

/**
 * Job info for a sync processor using hash
 */
internal class SyncJobHash<T, R>(
    private val hashStore: HashStore,
    private val hashType: HashType,
    private val getHashCode: suspend MenzaRaise.() -> String,
    fetchApi: suspend MenzaRaise.() -> T,
    convert: suspend MenzaRaise.(T) -> IorNel<MenzaError, R>,
    store: (R) -> Unit,
) : SyncJob<T, R>(
    { forced ->
        val hash = getHashCode()

        if (forced || hashStore.shouldReload(hashType, hash)) {
            // deferred job to save the new hash code
            val storeHashAction: suspend () -> Unit =
                { hashStore.storeHash(hashType, hash) }

            // process this job
            Some(storeHashAction)
        } else {
            // skip this job
            None
        }
    },
    fetchApi,
    convert,
    store,
)
