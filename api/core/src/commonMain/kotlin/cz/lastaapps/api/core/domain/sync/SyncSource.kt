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

package cz.lastaapps.api.core.domain.sync

import cz.lastaapps.core.util.extensions.flattenSensible
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart

interface SyncSource<T, Params> {
    fun getData(params: Params): Flow<T>
    suspend fun sync(params: Params, isForced: Boolean = false): SyncOutcome
}

fun <T, Params> SyncSource<T, Params>.getData(
    params: Flow<Params>,
): Flow<T> = params
    .map { getData(it) }
    .flattenSensible()
    .distinctUntilChanged()

suspend inline fun <T, Params> SyncSource<T, Params>.sync(
    params: Flow<Params>,
    isForced: Boolean = false,
) = sync(params.first(), isForced)
