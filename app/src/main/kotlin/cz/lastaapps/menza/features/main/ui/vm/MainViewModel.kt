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
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.main.domain.usecase.CheckLowBalanceUC
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.main.domain.usecase.IsFlipUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetAppSettingsUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetSettingsEverOpenedUC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class MainViewModel(
    context: VMContext,
    private val syncMenzaListUC: SyncMenzaListUC,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getSettingsOpened: GetSettingsEverOpenedUC,
    private val getAppSettings: GetAppSettingsUC,
    private val isFlip: IsFlipUC,
    private val checkLowBalanceUC: CheckLowBalanceUC,
    private val refreshWallet: WalletRefreshUC,
) : StateViewModel<MainState>(MainState(), context) {
    override suspend fun onFirstAppearance() {
        updateState { copy(isFlip = isFlip()) }
    }

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        scope.launch {
            syncMenzaListUC()
        }
        getSelectedMenza()
            .onEach {
                updateState { copy(selectedMenza = it, isReady = true) }
            }.launchIn(scope)

        getSettingsOpened()
            .onEach {
                updateState { copy(settingsViewed = it) }
            }.launchIn(scope)

        scope.launch {
            refreshWallet(false)
            checkLowBalanceUC().collectLatest {
                if (!lastState().lowBalanceShown) {
                    updateState { copy(showLowBalance = it) }
                }
            }
        }

        getAppSettings()
            .onEach {
                updateState { copy(alternativeNavigation = it.alternativeNavigation) }
            }.launchIn(scope)
    }

    fun dismissLowBalance() = updateState { copy(showLowBalance = false, lowBalanceShown = true) }
}

internal data class MainState(
    val isReady: Boolean = false,
    val settingsViewed: Boolean = false,
    val selectedMenza: Menza? = null,
    val alternativeNavigation: Boolean = false,
    val isFlip: Boolean = false,
    val showLowBalance: Boolean = false,
    val lowBalanceShown: Boolean = false,
) : VMState
