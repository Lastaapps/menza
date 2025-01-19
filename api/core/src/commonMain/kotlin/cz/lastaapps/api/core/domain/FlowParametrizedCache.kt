/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.api.core.domain

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

// Now I would really love to have traits
class FlowParametrizedCache<T, Param>(
    private val coroutineContext: CoroutineContext = Dispatchers.Default,
) {
    private var cacheLastParam: Option<Triple<Param, Flow<T>, CoroutineScope>> = None
    private val cacheMutex: Mutex = Mutex()

    suspend operator fun invoke(
        params: Param,
        block: suspend (param: Param) -> Flow<T>,
    ): Flow<T> =
        cacheMutex.withLock {
            when (val param = cacheLastParam) {
                is Some if (param.value.first == params) -> param.value.second
                else -> {
                    param.getOrNull()?.third?.cancel()
                    val scope = CoroutineScope(coroutineContext)
                    block(params)
                        .shareIn(scope, SharingStarted.Eagerly, replay = 1)
                        .also { data ->
                            cacheLastParam = Some(Triple(params, data, scope))
                        }
                }
            }
        }
}
