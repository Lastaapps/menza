/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.model.AppSettings
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType.Agata
import cz.lastaapps.menza.features.settings.domain.model.Currency
import cz.lastaapps.menza.features.settings.domain.model.InitialSelectionBehaviour
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.usecase.FullAppReloadUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetInitialMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetPreferredMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.menzaorder.GetOrderedVisibleMenzaListUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetAppSettingsUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.OnSettingsOpenedUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetAlternativeNavigationUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetBalanceWarningThresholdUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetCurrencyUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetAppThemeUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class SettingsViewModel(
    vmContext: VMContext,
    val onSettingsOpenedUC: OnSettingsOpenedUC,
    val getAppSettingsUC: GetAppSettingsUC,
    val getAppThemeUC: GetAppThemeUC,
    val getPreferredMenzaUC: GetPreferredMenzaUC,
    val setPriceTypeUC: SetPriceTypeUC,
    val setCurrencyUC: SetCurrencyUC,
    val setImagesOnMeteredUC: SetImagesOnMeteredUC,
    val setAlternativeNavigationUC: SetAlternativeNavigationUC,
    val setBalanceWarningThresholdUC: SetBalanceWarningThresholdUC,
    val setInitialMenzaUC: SetInitialMenzaUC,
    val setPreferredMenzaUC: SetPreferredMenzaUC,
    val getMenzaListUC: GetOrderedVisibleMenzaListUC,
    val fullAppReloadUC: FullAppReloadUC,
) : StateViewModel<SettingsState>(SettingsState(), vmContext) {
    override suspend fun whileSubscribed(scope: CoroutineScope) {
        getAppSettingsUC()
            .onEach {
                updateState { copy(appSettings = it) }
            }.launchIn(scope)
        getAppThemeUC()
            .onEach {
                updateState { copy(appTheme = it) }
            }.launchIn(scope)
        getPreferredMenzaUC()
            .onEach {
                updateState { copy(preferredMenza = it) }
            }.launchIn(scope)
        getMenzaListUC()
            .onEach {
                updateState { copy(menzaList = it) }
            }.launchIn(scope)
    }

    fun markAsViewed() =
        launchVM {
            onSettingsOpenedUC()
        }

    fun setPriceType(type: PriceType) =
        launchVM {
            setPriceTypeUC(type)
        }

    fun setCurrency(currency: Currency) =
        launchVM {
            setCurrencyUC(currency)
        }

    fun setDownloadOnMetered(enable: Boolean) =
        launchVM {
            setImagesOnMeteredUC(enable)
        }

    fun setBalanceWarningThreshold(threshold: Int) =
        launchVM {
            setBalanceWarningThresholdUC(threshold)
        }

    fun setInitMenzaBehaviour(behaviour: InitialSelectionBehaviour) =
        launchVM {
            setInitialMenzaUC(behaviour)
        }

    fun setSelectedMenza(menza: Menza) =
        launchVM {
            setPreferredMenzaUC(menza.type)
        }

    fun setAlternativeNavigation(enabled: Boolean) =
        launchVM {
            setAlternativeNavigationUC(enabled)
        }

    fun fullAppReload() =
        launchVM {
            fullAppReloadUC()
        }
}

internal data class SettingsState(
    val isReady: Boolean = false,
    val appSettings: AppSettings? = null,
    val appTheme: AppThemeType = Agata,
    val preferredMenza: Menza? = null,
    val menzaList: ImmutableList<Menza> = persistentListOf(),
) : VMState
