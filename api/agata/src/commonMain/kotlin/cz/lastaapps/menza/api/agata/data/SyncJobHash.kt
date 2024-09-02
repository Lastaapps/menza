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

package cz.lastaapps.menza.api.agata.data

import arrow.core.IorNel
import arrow.core.None
import arrow.core.Some
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.error.MenzaRaise
import cz.lastaapps.menza.api.agata.data.model.HashType
import cz.lastaapps.menza.api.agata.domain.HashStore

/**
 * Job info for a sync processor using hash
 */
internal class SyncJobHash<T, R, Params>(
    private val hashStore: HashStore,
    private val hashType: (Params) -> HashType,
    private val getHashCode: suspend MenzaRaise.(Params) -> String,
    fetchApi: suspend MenzaRaise.(Params) -> T,
    convert: suspend MenzaRaise.(Params, T) -> IorNel<DomainError, R>,
    store: (Params, R) -> Unit,
) : SyncJob<T, R, Params>(
        { params, forced ->
            val hash = getHashCode(params)

            if (forced || hashStore.shouldReload(hashType(params), hash)) {
                // deferred job to save the new hash code
                val storeHashAction: suspend () -> Unit =
                    { hashStore.storeHash(hashType(params), hash) }

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
