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

package cz.lastaapps.menza.features.settings.ui.vm

import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.DarkMode.Dark
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.usecase.FullAppReloadUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetInitialMenzaModeUI
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetInitialMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedVisibleMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetBalanceWarningThresholdUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.OnSettingsOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetBalanceWarningThresholdUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetDarkModeUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.onEach

internal class SettingsViewModel(
    vmContext: VMContext,
    val onSettingsOpenedUC: OnSettingsOpenedUC,
    val getAppThemeUC: GetAppThemeUC,
    val getDarkModeUC: GetDarkModeUC,
    val getPriceTypeUC: GetPriceTypeUC,
    val setPriceTypeUC: SetPriceTypeUC,
    val getImagesOnMeteredUC: GetImagesOnMeteredUC,
    val setImagesOnMeteredUC: SetImagesOnMeteredUC,
    val getBalanceWarningThresholdUC: GetBalanceWarningThresholdUC,
    val setBalanceWarningThresholdUC: SetBalanceWarningThresholdUC,
    val getMenzaListUC: GetOrderedVisibleMenzaListUC,
    val getInitialMenzaUC: GetInitialMenzaModeUI,
    val setInitialMenzaUC: SetInitialMenzaUC,
    val getPreferredMenzaUC: GetPreferredMenzaUC,
    val setPreferredMenzaUC: SetPreferredMenzaUC,
    val fullAppReloadUC: FullAppReloadUC,
) : StateViewModel<SettingsState>(SettingsState(), vmContext), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() = launchVM {
        getAppThemeUC().onEach {
            updateState { copy(appTheme = it) }
        }.launchInVM()
        getDarkModeUC().onEach {
            updateState { copy(darkMode = it) }
        }.launchInVM()
        getPriceTypeUC().onEach {
            updateState { copy(priceType = it) }
        }.launchInVM()
        getImagesOnMeteredUC().onEach {
            updateState { copy(downloadOnMetered = it) }
        }.launchInVM()
        getBalanceWarningThresholdUC().onEach {
            updateState { copy(balanceWarningThreshold = it) }
        }.launchInVM()
        getInitialMenzaUC().onEach {
            updateState { copy(initialMenzaBehaviour = it) }
        }.launchInVM()
        getPreferredMenzaUC().onEach {
            updateState { copy(selectedMenza = it) }
        }.launchInVM()
        getMenzaListUC().onEach {
            updateState { copy(menzaList = it) }
        }.launchInVM()
    }

    fun markAsViewed() = launchVM {
        onSettingsOpenedUC()
    }

    fun setPriceType(type: PriceType) = launchVM {
        setPriceTypeUC(type)
    }

    fun setDownloadOnMetered(enable: Boolean) = launchVM {
        setImagesOnMeteredUC(enable)
    }

    fun setBalanceWarningThreshold(threshold: Int) = launchVM {
        setBalanceWarningThresholdUC(threshold)
    }

    fun setInitMenzaBehaviour(behaviour: InitialSelectionBehaviour) = launchVM {
        setInitialMenzaUC(behaviour)
    }

    fun setSelectedMenza(menza: Menza) = launchVM {
        setPreferredMenzaUC(menza.type)
    }

    fun fullAppReload() = launchVM {
        fullAppReloadUC()
    }
}

internal data class SettingsState(
    val isReady: Boolean = false,
    val appTheme: AppThemeType = Agata,
    val darkMode: DarkMode = Dark,
    val priceType: PriceType = PriceType.Unset,
    val downloadOnMetered: Boolean = false,
    val balanceWarningThreshold: Int = 0,
    val initialMenzaBehaviour: InitialSelectionBehaviour = InitialSelectionBehaviour.Ask,
    val menzaList: ImmutableList<Menza> = persistentListOf(),
    val selectedMenza: Menza? = null,
) : VMState