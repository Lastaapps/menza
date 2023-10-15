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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.ExplicitGroupsComposable
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewModelScope
import com.bumble.appyx.navigation.node.Node
import cz.lastaapps.menza.features.today.ui.navigation.TodayNode
import cz.lastaapps.menza.ui.locals.koinActivityViewModel
import kotlin.reflect.KClass
import kotlin.time.Duration.Companion.seconds
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.LocalKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.lighthousegames.logging.logging

/**
 * Uses counted references to store viewModels and clear them when no longer used
 * ViewModel is cleared if:
 * - number of references is 0 and the configuration is not changing
 * - if number of references remains 0 for at least NodeViewModel.CLEANUP_DELAY
 */
class NodeViewModel : ViewModel() {

    private val lock = this as SynchronizedObject

    private val map = HashMap<String, ViewModelStoreOwner>()
    private val counter = HashMap<String, Int>()
    private val cleanupJobs = HashMap<String, Job>()

    fun inc(nodeId: String, name: KClass<out Any>) = synchronized(lock) {
        log.i { "Inc $nodeId ${name.simpleName}" }

        val cnt = counter[nodeId]?.inc() ?: error("Store not created")

        cleanupJobs.remove(nodeId)?.cancel()

        check(cnt >= 0) { "Negative number of references" }

        counter[nodeId] = cnt
    }

    fun dec(
        nodeId: String,
        delayCleanup: Boolean,
        name: KClass<out Any>,
    ) = synchronized(lock) {
        log.i { "Dec $nodeId ${name.simpleName}" }

        val cnt = counter[nodeId]?.dec() ?: error("Decreasing non-existing key")
        check(cnt >= 0) { "Negative number of references" }

        counter[nodeId] = cnt
        if (cnt == 0) {
            if (delayCleanup) {
                cleanupJobs[nodeId]?.cancel()
                cleanupJobs[nodeId] = viewModelScope.launch {
                    log.i { "Scheduling cleanup in $CLEANUP_DELAY for $nodeId ${name.simpleName}" }
                    delay(CLEANUP_DELAY)

                    synchronized(this@NodeViewModel) {
                        clear(nodeId, name)
                    }
                }
            } else {
                clear(nodeId, name)
            }
        }
    }

    private fun clear(nodeId: String, name: KClass<out Any>) {
        log.i { "Clearing $nodeId ${name.simpleName}" }
        map.remove(nodeId)?.viewModelStore?.clear()
        counter.remove(nodeId)
        cleanupJobs.remove(nodeId)
    }

    fun getStoreOwner(
        nodeId: String,
        name: KClass<out Any>,
    ): ViewModelStoreOwner = synchronized(lock) {
        log.i { "Getting $nodeId ${name.simpleName}" }

        map.getOrPut(nodeId) {
            log.i { "Creating $nodeId ${name.simpleName}" }
            counter[nodeId] = 0
            object : ViewModelStoreOwner {
                override val viewModelStore: ViewModelStore = ViewModelStore()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        synchronized(lock) {
            log.i { "Clearing all" }

            // cancel cleanup jobs
            cleanupJobs.values.forEach { it.cancel() }
            cleanupJobs.clear()

            map.values.forEach {
                it.viewModelStore.clear()
            }
            map.clear()
            counter.clear()
        }
    }

    companion object {
        private val log = logging()
        private val CLEANUP_DELAY = 8.seconds
    }
}

@Composable
@NonRestartableComposable
inline fun <reified T : ViewModel> Node.nodeViewModel(
    qualifier: Qualifier? = null,
    key: String? = null,
    scope: Scope = LocalKoinScope.current,
    noinline parameters: ParametersDefinition? = null,
): T {
    val nodeClass = this@nodeViewModel::class

    val store: NodeViewModel = koinActivityViewModel()
    val owner = remember(id) {
        store.getStoreOwner(id, nodeClass)
    }

    DisposableEffect(id) {
        store.inc(id, nodeClass)
        onDispose {
            store.dec(id, delayCleanup = integrationPoint.isChangingConfigurations, nodeClass)
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
