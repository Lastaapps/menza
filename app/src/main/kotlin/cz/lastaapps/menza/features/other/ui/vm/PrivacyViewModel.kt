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

package cz.lastaapps.menza.features.other.ui.vm

import androidx.lifecycle.viewModelScope
import cz.lastaapps.core.ui.vm.BaseViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.menza.features.starting.data.PrivacyStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.Clock
import org.lighthousegames.logging.logging

internal class PrivacyViewModel(
    private val store: PrivacyStore,
    private val clock: Clock,
    context: VMContext,
) : BaseViewModel(context) {

    companion object {
        private val log = logging()
    }

    val shouldShow = store.approved.map { it == null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun onApprove() = launchVM {
        log.i { "Setting approved" }

        store.setApproved(clock.now())
    }
}
