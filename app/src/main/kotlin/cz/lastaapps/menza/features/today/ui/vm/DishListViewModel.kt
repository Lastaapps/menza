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

package cz.lastaapps.menza.features.today.ui.vm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.toOption
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.core.domain.sync.mapSync
import cz.lastaapps.api.main.domain.usecase.GetTodayDishListUC
import cz.lastaapps.api.main.domain.usecase.OpenMenuUC
import cz.lastaapps.api.main.domain.usecase.SyncTodayDishListUC
import cz.lastaapps.core.data.AppInfoProvider
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.usecase.IsOnMeteredUC
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.settings.SetOliverRow
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.features.today.domain.usecase.GetTodayUserSettingsUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

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
    private val appInfoProvider: AppInfoProvider,
) : StateViewModel<DishListState>(DishListState(), context),
    ErrorHolder {
    private val log = localLogger()

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        scope.launch {
            getSelectedMenzaUC()
                // this cannot be onEach
                // the code runs blocking code inside
                // and onEach waits for it's body to finish
                .collectLatest { newMenza ->
                    log.i { "Registered a new: $newMenza" }

                    updateState {
                        if (selectedMenza?.getOrNull()?.type != newMenza?.type || newMenza == null) {
                            copy(
                                selectedMenza = newMenza.toOption(),
                                items = persistentListOf(),
                            )
                        } else {
                            this
                        }
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
        }

        getUserSettingsUC()
            .onEach {
                updateState { copy(userSettings = it) }
            }.launchIn(scope)
        isOnMeteredUC()
            .onEach {
                updateState { copy(isOnMetered = it) }
            }.launchIn(scope)

        // Refreshes the screen if user is looking at the data for at least 42 seconds
        if (!appInfoProvider.isDebug()) {
            flow
                .map { it.selectedMenza?.getOrNull() }
                .distinctUntilChanged()
                .mapLatest { menza ->
                    while (menza != null) {
                        delay(42.seconds)
                        load(menza, true)
                    }
                }.launchIn(scope)
        }
    }

    private var syncJob: Job? = null

    fun reload() {
        if (lastState().isLoading) return
        syncJob =
            launchJob {
                lastState().selectedMenza?.getOrNull()?.let {
                    load(it, true)
                }
            }
    }

    fun openWebMenu() =
        launchVM {
            lastState().selectedMenza?.getOrNull()?.let { openMenuLinkUC(it) }
        }

    fun setCompactView(mode: DishListMode) =
        launchVM {
            setDishListModeUC(mode)
        }

    fun setImageScale(scale: Float) =
        launchVM {
            setImageScaleUC(scale)
        }

    fun setOliverRow(used: Boolean) =
        launchVM {
            setOliverRowUC(used)
        }

    private suspend fun load(
        menza: Menza,
        isForced: Boolean,
    ) {
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

@Immutable
internal data class DishListState(
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val selectedMenza: Option<Menza>? = null,
    val items: ImmutableList<DishCategory> = persistentListOf(),
    val userSettings: TodayUserSettings = TodayUserSettings(),
    val isOnMetered: Boolean = false,
) : VMState {
    val showExperimentalWarning: Boolean =
        selectedMenza?.getOrNull()?.isExperimental ?: false
}
