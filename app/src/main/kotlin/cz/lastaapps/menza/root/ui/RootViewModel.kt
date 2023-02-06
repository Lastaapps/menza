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

package cz.lastaapps.menza.root.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.menza.root.domain.usecase.IsAppSetUpUC
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO implement state view model
internal class RootViewModel(
    private val isAppSetUp: IsAppSetUpUC,
) : ViewModel() {
    val isReady = MutableStateFlow(false)
    val isSetUp = MutableStateFlow(false)

    fun appeared() = viewModelScope.launch {
        isSetUp.value = isAppSetUp()
        isReady.value = true
    }
}
