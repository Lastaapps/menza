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

package cz.lastaapps.menza.features.main.ui.vm

import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.main.domain.usecase.SyncMenzaListUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletRefreshUC
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.main.domain.usecase.CheckLowBalanceUC
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.main.domain.usecase.IsFlipUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetSettingsEverOpenedUC
import kotlinx.coroutines.flow.collectLatest

internal class MainViewModel(
    context: VMContext,
    private val syncMenzaListUC: SyncMenzaListUC,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getSettingsOpened: GetSettingsEverOpenedUC,
    private val isFlip: IsFlipUC,
    private val checkLowBalanceUC: CheckLowBalanceUC,
    private val refreshWallet: WalletRefreshUC,
) : StateViewModel<MainState>(MainState(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() {
        launchVM {
            syncMenzaListUC()
        }
        launchVM {
            getSelectedMenza().collectLatest {
                updateState { copy(selectedMenza = it, isReady = true) }
            }
        }
        launchVM {
            getSettingsOpened().collectLatest {
                updateState { copy(settingsViewed = it) }
            }
        }
        updateState { copy(isFlip = isFlip()) }
        launchVM {
            refreshWallet(false)
            checkLowBalanceUC().collectLatest {
                updateState { copy(showLowBalance = it) }
            }
        }
    }

    fun dismissLowBalance() = updateState { copy(showLowBalance = false) }
}

internal data class MainState(
    val isReady: Boolean = false,
    val settingsViewed: Boolean = false,
    val selectedMenza: Menza? = null,
    val isFlip: Boolean = false,
    val showLowBalance: Boolean = false,
) : VMState
