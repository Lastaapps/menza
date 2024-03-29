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

package cz.lastaapps.api.core.domain.sync

import arrow.core.IorNel
import arrow.core.Option
import arrow.core.some
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.error.MenzaRaise

abstract class SyncJob<T, R>(
    // Some -> will be run
    // None -> should be skipped
    val shouldRun: suspend MenzaRaise.(force: Boolean) -> Option<suspend () -> Unit>,
    val fetchApi: suspend MenzaRaise.() -> T,
    val convert: suspend MenzaRaise.(T) -> IorNel<DomainError, R>,
    val store: (R) -> Unit,
)

/**
 * Job info for a sync processor, no cache check
 */
class SyncJobNoCache<T, R>(
    fetchApi: suspend MenzaRaise.() -> T,
    convert: suspend MenzaRaise.(T) -> IorNel<DomainError, R>,
    store: (R) -> Unit,
) : SyncJob<T, R>(
    {
        val noOp: suspend () -> Unit = {}
        noOp.some()
    },
    fetchApi,
    convert,
    store,
)
