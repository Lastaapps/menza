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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@JvmInline
value class VMContext(val context: CoroutineContext)

abstract class BaseViewModel(private val context: VMContext) : ViewModel() {
    protected fun launchVM(block: suspend CoroutineScope.() -> Unit) {
        launchJob(block)
    }

    protected fun launchJob(block: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context.context, block = block)

    protected fun <T> Flow<T>.launchInVM() {
        viewModelScope.launch(context.context) { collect() }
    }
}
