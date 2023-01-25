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

package cz.lastaapps.menza.api.agata.domain.model

import cz.lastaapps.core.domain.Outcome

internal sealed interface SyncJob<T> {
    val fetchApi: suspend () -> Outcome<T>
    val store: (T) -> Unit
}

/**
 * Job info for a sync processor using hash
 */
internal class SyncJobHash<T>(
    val hashType: HashType,
    val getHashCode: suspend () -> Outcome<String>,
    override val fetchApi: suspend () -> Outcome<T>,
    override val store: (T) -> Unit,
) : SyncJob<T>

/**
 * Job info for a sync processor, no cache check
 */
internal class SyncJobNoCache<T>(
    override val fetchApi: suspend () -> Outcome<T>,
    override val store: (T) -> Unit,
) : SyncJob<T>
