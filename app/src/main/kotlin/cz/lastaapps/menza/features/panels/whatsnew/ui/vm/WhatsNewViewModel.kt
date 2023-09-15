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

package cz.lastaapps.menza.features.panels.whatsnew.ui.vm

import GetAppVersionUC
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import com.bumble.appyx.navigation.node.Node
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.other.data.WhatsNewDataStore
import cz.lastaapps.menza.features.other.domain.model.WhatsNewInfo
import cz.lastaapps.menza.features.panels.whatsnew.domain.LoadWhatsNewUC
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel.State
import cz.lastaapps.menza.ui.util.nodeViewModel
import java.util.Locale
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

internal class WhatsNewViewModel(
    private val locales: ImmutableList<Locale>,
    context: VMContext,
    private val store: WhatsNewDataStore,
    private val loadWhatsNewUC: LoadWhatsNewUC,
    private val getAppVersionUC: GetAppVersionUC,
) : StateViewModel<State>(State(), context), Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() {
        launchVM {
            val map = loadWhatsNewUC()
            val locale = locales.firstOrNull { map.containsKey(it) } ?: Locale.US
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

@Composable
internal fun Node.whatsNewViewModel(): WhatsNewViewModel {
    val config = LocalConfiguration.current

    val locales: ImmutableList<Locale> = remember(config) {
        val languages = ConfigurationCompat.getLocales(config)
        List(languages.size()) { languages[it] }
            .filterNotNull()
            .toImmutableList()
    }

    return nodeViewModel<WhatsNewViewModel> {
        parametersOf(locales)
    }
}
