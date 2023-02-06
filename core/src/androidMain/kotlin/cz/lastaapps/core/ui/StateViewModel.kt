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

package cz.lastaapps.core.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@JvmInline
value class VMContext(val context: CoroutineContext)

abstract class StateViewModel<State : Any>(
    val init: State,
    private val context: VMContext,
) : ViewModel() {
    private val myState = MutableStateFlow(init)

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(context.context, block = block)
    }

    protected fun lastState() = myState.value
    protected fun updateState(block: State.(State) -> State) =
        myState.update { with(it) { block(it) } }

    val flow = myState.asStateFlow()
    val flowState
        @Composable
        get() = myState.collectAsStateWithLifecycle()
}
