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
import androidx.compose.runtime.LaunchedEffect
import kotlin.reflect.KFunction1
import kotlin.reflect.KProperty1

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun <State : Any, VM : StateViewModel<State>> HandleDismiss(
    viewModel: VM,
    getVal: KProperty1<State, Boolean>,
    dismiss: KFunction1<VM, Unit>,
    noinline launch: () -> Unit,
) {
    val isSelected = getVal(viewModel.flowState.value)
    LaunchedEffect(isSelected) {
        if (isSelected) {
            dismiss(viewModel)
            launch()
        }
    }
}
