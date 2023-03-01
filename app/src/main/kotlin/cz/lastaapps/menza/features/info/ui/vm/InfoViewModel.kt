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

package cz.lastaapps.menza.features.info.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.toOption
import cz.lastaapps.api.core.domain.model.Info
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.sync.mapSync
import cz.lastaapps.api.main.domain.usecase.GetInfoUC
import cz.lastaapps.api.main.domain.usecase.SyncInfoUC
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

internal class InfoViewModel(
    context: VMContext,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getInfo: GetInfoUC,
    private val syncInfo: SyncInfoUC,
) : StateViewModel<InfoState>(InfoState(), context), Appearing, ErrorHolder {
    override var hasAppeared: Boolean = false

    companion object {
        private val log = logging()
    }

    override fun onAppeared() = launchVM {
        launchVM {
            getSelectedMenza().collectLatest {
                log.i { "Registered a new: $it" }

                updateState {
                    copy(
                        selectedMenza = it.toOption(),
                        items = null,
                    )
                }
                syncJob?.cancel()
                if (it != null) {
                    coroutineScope {
                        this.launch {
                            load(it, false)
                        }
                        getInfo(it).collectLatest { items ->
                            updateState { copy(items = items) }
                        }
                    }
                }
            }
        }
    }

    private var syncJob: Job? = null
    fun reload() {
        if (lastState().isLoading) return
        syncJob = launchJob {
            lastState().selectedMenza?.orNull()?.let {
                load(it, true)
            }
        }
    }

    private suspend fun load(menza: Menza, isForced: Boolean) {
        withLoading({ copy(isLoading = it) }) {
            when (val res = syncInfo(menza, isForced = isForced).mapSync()) {
                is Left -> updateState { copy(error = res.value) }
                is Right -> {}
            }
        }
    }

    @Composable
    override fun getError(): MenzaError? = flowState.value.error
    override fun dismissError() = updateState { copy(error = null) }
}

internal data class InfoState(
    val selectedMenza: Option<Menza>? = null,
    val isLoading: Boolean = false,
    val items: Info? = null,
    val error: MenzaError? = null,
)
