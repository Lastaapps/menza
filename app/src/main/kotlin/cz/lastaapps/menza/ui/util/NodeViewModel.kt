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

package cz.lastaapps.menza.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bumble.appyx.navigation.node.Node
import cz.lastaapps.menza.ui.locals.koinActivityViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.LocalKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.lighthousegames.logging.logging

class NodeViewModel : ViewModel() {

    private val map = HashMap<String, ViewModelStoreOwner>()

    fun clear(nodeId: String) {
        log.i { "Clearing $nodeId" }
        map.remove(nodeId)?.viewModelStore?.clear()
    }

    fun getStoreOwner(nodeId: String): ViewModelStoreOwner = run {
        log.i { "Getting $nodeId" }

        map.getOrPut(nodeId) {
            log.i { "Creating $nodeId" }
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        log.i { "Clearing all" }
        map.values.forEach {
            it.viewModelStore.clear()
        }
        map.clear()
    }

    companion object {
        private val log = logging()
    }
}

@Composable
inline fun <reified T : ViewModel> Node.nodeViewModel(
    qualifier: Qualifier? = null,
    key: String? = null,
    scope: Scope = LocalKoinScope.current,
    noinline parameters: ParametersDefinition? = null,
): T {
    val store: NodeViewModel = koinActivityViewModel()
    val owner = remember(id) {
        store.getStoreOwner(id)
    }

    DisposableEffect(id) {
        onDispose {
            if (!integrationPoint.isChangingConfigurations) {
                store.clear(id)
            }
        }
    }

    return koinViewModel<T>(
        viewModelStoreOwner = owner,
        qualifier = qualifier,
        key = key,
        scope = scope,
        parameters = parameters,
    )
}
