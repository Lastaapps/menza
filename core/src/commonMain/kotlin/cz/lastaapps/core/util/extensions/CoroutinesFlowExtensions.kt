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

package cz.lastaapps.core.util.extensions

import arrow.core.left
import arrow.core.right
import cz.lastaapps.core.domain.error.CommonError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration

fun <T1, T2, T3, T4, T5, T6, R> combine6(
    flow1: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> =
    combine(
        combine(flow1, flow2, flow3, ::Triple),
        combine(flow4, flow5, flow6, ::Triple),
    ) { (v1, v2, v3), (v4, v5, v6) ->
        transform(v1, v2, v3, v4, v5, v6)
    }

suspend fun <T> withTimeoutOutcome(
    timeout: Duration,
    block: suspend CoroutineScope.() -> T,
) = try {
    withTimeout(timeout, block).right()
} catch (e: TimeoutCancellationException) {
    CommonError.WorkTimeout(e).left()
}

fun <T> Flow<Flow<T>>.flattenSensible(): Flow<T> =
    channelFlow {
        // the first collect has to be collectLatest!!!
        collectLatest { flow -> flow.collect { send(it) } }
    }

fun <T, Acu> Iterable<Flow<T>>.foldFlows(
    initial: Acu,
    operation: (Acu, T) -> Acu,
): Flow<Acu> =
    run {
        val baseCase = flow { emit(initial) }

        fold(baseCase) { acu, data ->
            combine(acu, data, operation)
        }
    }

fun <T, Acu> Sequence<Flow<T>>.foldFlows(
    initial: Acu,
    operation: (Acu, T) -> Acu,
): Flow<Acu> = asIterable().foldFlows(initial, operation)

/**
 * Like fold, but merges flows in pairs to improve
 * update complexity from O(n) to O(log(n))
 */
fun <T, Acu> List<Flow<T>>.foldBinary(
    initial: T,
    mapper: (T) -> Acu,
    operation: (Acu, Acu) -> Acu,
): Flow<Acu> =
    when (size) {
        0 -> flow { emit(mapper(initial)) }
        1 -> this[0].map(mapper)
        else ->
            combine(
                subList(0, size / 2).foldBinary(initial, mapper, operation),
                subList(size / 2, size).foldBinary(initial, mapper, operation),
                operation,
            )
    }

fun <T> List<Flow<T>>.foldBinary(
    initial: T,
    operation: (T, T) -> T,
): Flow<T> = foldBinary(initial, { it }, operation)

/**
 * Similar to onStart, but provides a coroutine scope
 * with the same lifetime as the collected flow,
 * eg. closed after onCompletion is called
 */
fun <T> Flow<T>.whileSubscribed(onStart: suspend CoroutineScope.() -> Unit) =
    flow {
        val scope = CoroutineScope(currentCoroutineContext())
        onStart(scope)
        try {
            collect(this)
        } catch (e: Throwable) {
            scope.cancel()
            throw e
        }
        scope.cancel()
    }
