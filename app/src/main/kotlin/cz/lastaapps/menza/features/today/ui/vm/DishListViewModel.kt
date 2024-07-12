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

package cz.lastaapps.menza.features.today.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.toOption
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.sync.mapSync
import cz.lastaapps.api.main.domain.usecase.GetTodayDishListUC
import cz.lastaapps.api.main.domain.usecase.OpenMenuUC
import cz.lastaapps.api.main.domain.usecase.SyncTodayDishListUC
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.usecase.IsOnMeteredUC
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.usecase.SetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.SetOliverRow
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.features.today.domain.usecase.GetTodayUserSettingsUC
import kotlin.time.Duration.Companion.seconds
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class DishListViewModel(
    context: VMContext,
    private val getSelectedMenzaUC: GetSelectedMenzaUC,
    private val getTodayDishListUC: GetTodayDishListUC,
    private val syncTodayDishListUC: SyncTodayDishListUC,
    private val setOliverRowUC: SetOliverRow,
    private val setImageScaleUC: SetImageScaleUC,
    private val setDishListModeUC: SetDishListModeUC,
    private val isOnMeteredUC: IsOnMeteredUC,
    private val getUserSettingsUC: GetTodayUserSettingsUC,
    private val openMenuLinkUC: OpenMenuUC,
) : StateViewModel<DishListState>(DishListState(), context), Appearing, ErrorHolder {
    override var hasAppeared: Boolean = false

    private val log = localLogger()

    override fun onAppeared() = launchVM {
        getSelectedMenzaUC().collectLatestInVM { newMenza ->
            log.i { "Registered a new: $newMenza" }

            updateState {
                copy(
                    selectedMenza = newMenza.toOption(),
                    items = persistentListOf(),
                )
            }
            syncJob?.cancel()
            if (newMenza != null) {
                coroutineScope {
                    this.launch {
                        load(newMenza, false)
                    }
                    getTodayDishListUC(newMenza).collectLatest { items ->
                        updateState { copy(items = items) }
                    }
                }
            }
        }

        getUserSettingsUC().onEach {
            updateState { copy(userSettings = it) }
        }.launchInVM()
        isOnMeteredUC().onEach {
            updateState { copy(isOnMetered = it) }
        }.launchInVM()

        // Refreshes the screen if user is looking at the data for at least 42 seconds
        flow.map {
            it.isResumed to it.selectedMenza?.getOrNull()
        }
            .distinctUntilChanged()
            .collectLatestInVM { (resumed, menza) ->
                while (resumed && menza != null) {
                    delay(42.seconds)
                    load(menza, true)
                }
            }
    }

    private var syncJob: Job? = null
    fun reload() {
        if (lastState().isLoading) return
        syncJob = launchJob {
            lastState().selectedMenza?.getOrNull()?.let {
                load(it, true)
            }
        }
    }

    fun openWebMenu() = launchVM {
        lastState().selectedMenza?.getOrNull()?.let { openMenuLinkUC(it) }
    }

    fun setCompactView(mode: DishListMode) = launchVM {
        setDishListModeUC(mode)
    }

    fun setImageScale(scale: Float) = launchVM {
        setImageScaleUC(scale)
    }

    fun setOliverRow(used: Boolean) = launchVM {
        setOliverRowUC(used)
    }

    fun setIsResumed(resumed: Boolean) =
        updateState { copy(isResumed = resumed) }

    private suspend fun load(menza: Menza, isForced: Boolean) {
        withLoading({ copy(isLoading = it) }) {
            when (val res = syncTodayDishListUC(menza, isForced = isForced).mapSync()) {
                is Left -> updateState { copy(error = res.value) }
                is Right -> {}
            }
        }
    }

    @Composable
    override fun getError(): DomainError? = flowState.value.error
    override fun dismissError() = updateState { copy(error = null) }
}

internal data class DishListState(
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val selectedMenza: Option<Menza>? = null,
    val items: ImmutableList<DishCategory> = persistentListOf(),
    val userSettings: TodayUserSettings = TodayUserSettings(),
    val isOnMetered: Boolean = false,
    // is the UI consuming this viewModel is resumed
    val isResumed: Boolean = false,
) : VMState {
    val showExperimentalWarning: Boolean =
        selectedMenza?.getOrNull()?.isExperimental ?: false
}
