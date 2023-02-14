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

package cz.lastaapps.core.ui.vm

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import arrow.fx.coroutines.resource
import arrow.fx.coroutines.use
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class StateViewModel<State : Any>(
    val init: State,
    context: VMContext,
) : BaseViewModel(context) {
    private val myState = MutableStateFlow(init)

    protected fun lastState() = myState.value
    protected fun updateState(block: State.() -> State) =
        myState.update(block)

    val flow = myState.asStateFlow()
    val flowState
        @Composable
        get() = myState.collectAsStateWithLifecycle()

    suspend fun <R> withLoading(
        loading: State.(isLoading: Boolean) -> State,
        block: suspend () -> R,
    ) = resource(
        acquire = { updateState { loading(true) } },
        release = { _, _ -> updateState { loading(false) } },
    ).use { block() }
}