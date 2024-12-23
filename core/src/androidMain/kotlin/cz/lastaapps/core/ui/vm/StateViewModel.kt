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

package cz.lastaapps.core.ui.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.resourceScope
import cz.lastaapps.core.util.extensions.whileSubscribed
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

abstract class StateViewModel<State : VMState>(
    init: State,
    context: VMContext,
) : BaseViewModel(context) {
    private val myState = MutableStateFlow(init)

    protected fun lastState() = myState.value

    protected fun updateState(block: State.() -> State) = myState.update(block)

    /**
     * Is called once an external entity starts collecting local state.
     * Provides a coroutine scope that is cancelled 5 seconds after
     * there are no more collectors.
     */
    protected open suspend fun onFirstAppearance() {}

    /**
     * Is called once an external entity starts collecting local state.
     * Provides a coroutine scope that is cancelled 5 seconds after
     * there are no more collectors.
     */
    protected open suspend fun whileSubscribed(scope: CoroutineScope) {}

    private var didAppear: Boolean = false
    val flow =
        myState
            .whileSubscribed(Dispatchers.Main) {
                if (!didAppear) {
                    didAppear = true
                    onFirstAppearance()
                }
                whileSubscribed(this)
            }.flowOn(Dispatchers.Main)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5.seconds),
                myState.value,
            )

    val flowState
        @Composable
        get() = flow.collectAsStateWithLifecycle()

    protected suspend fun <R> withLoading(
        loading: State.(isLoading: Boolean) -> State,
        block: suspend (State) -> R,
    ) = resource(
        acquire = { updateState { loading(true) } },
        release = { _, _ -> updateState { loading(false) } },
    ) // .use { block() }
        .let {
            resourceScope {
                it.bind()
                block(lastState())
            }
        }
}

@Immutable
interface VMState
