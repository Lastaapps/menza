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

package cz.lastaapps.menza.features.panels.whatsnew.ui.vm

import cz.lastaapps.core.data.DeviceLocalesProvider
import cz.lastaapps.core.domain.usecase.GetAppVersionUC
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.other.data.WhatsNewDataStore
import cz.lastaapps.menza.features.other.domain.model.WhatsNewInfo
import cz.lastaapps.menza.features.panels.whatsnew.domain.LoadWhatsNewUC
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel.State
import java.util.Locale
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.onEach

internal class WhatsNewViewModel(
    context: VMContext,
    private val localesProvider: DeviceLocalesProvider, // yes, this should be a usecase
    private val store: WhatsNewDataStore,
    private val loadWhatsNewUC: LoadWhatsNewUC,
    private val getAppVersionUC: GetAppVersionUC,
) : StateViewModel<State>(State(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() {
        launchVM {
            val map = loadWhatsNewUC()
            val locale =
                localesProvider.provideLocales().firstOrNull { map.containsKey(it) } ?: Locale.US
            val data = map.getOrDefault(locale, emptySet()).sorted()

            updateState {
                copy(news = data)
            }
        }

        store.lastViewed.onEach { lastViewed ->
            updateState {
                copy(shouldShow = getAppVersionUC() > lastViewed)
            }
        }.launchInVM()
    }

    fun onDismiss() = launchVM {
        store.setLastViewed(getAppVersionUC())
    }

    data class State(
        val news: List<WhatsNewInfo> = persistentListOf(),
        val shouldShow: Boolean = false,
    ) : VMState
}
