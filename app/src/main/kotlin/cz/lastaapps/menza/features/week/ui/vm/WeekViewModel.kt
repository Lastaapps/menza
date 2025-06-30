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

package cz.lastaapps.menza.features.week.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.Option
import arrow.core.toOption
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.WeekDayDish
import cz.lastaapps.api.core.domain.sync.mapSync
import cz.lastaapps.api.main.domain.usecase.GetWeekDishListUC
import cz.lastaapps.api.main.domain.usecase.OpenMenuUC
import cz.lastaapps.api.main.domain.usecase.SyncWeekDishListUC
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.settings.domain.model.Currency
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetAppSettingsUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class WeekViewModel(
    context: VMContext,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getWeekDish: GetWeekDishListUC,
    private val syncWeekDish: SyncWeekDishListUC,
    private val openMenuLink: OpenMenuUC,
    private val getAppSettingsUC: GetAppSettingsUC,
) : StateViewModel<WeekState>(WeekState(), context),
    ErrorHolder {
    private val log = localLogger()

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        getSelectedMenza()
            .mapLatest {
                log.i { "Registered a new: $it" }

                updateState {
                    copy(
                        selectedMenza = it.toOption(),
                        items = persistentListOf(),
                    )
                }
                syncJob?.cancel()
                if (it != null) {
                    coroutineScope {
                        this.launch {
                            load(it, false)
                        }
                        getWeekDish(it).collectLatest { items ->
                            updateState { copy(items = items) }
                        }
                    }
                }
            }.launchIn(scope)

        getAppSettingsUC()
            .onEach {
                updateState {
                    copy(
                        priceType = it.priceType,
                        currency = it.currency,
                    )
                }
            }.launchIn(scope)
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
            lastState().selectedMenza?.getOrNull()?.let { openMenuLink(it) }
        }

    private suspend fun load(
        menza: Menza,
        isForced: Boolean,
    ) {
        withLoading({ copy(isLoading = it) }) {
            when (val res = syncWeekDish(menza, isForced = isForced).mapSync()) {
                is Left -> updateState { copy(error = res.value) }
                is Right -> {}
            }
        }
    }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() = updateState { copy(error = null) }
}

internal data class WeekState(
    val selectedMenza: Option<Menza>? = null,
    val priceType: PriceType = PriceType.Unset,
    val currency: Currency = Currency.NONE,
    val isLoading: Boolean = false,
    val error: DomainError? = null,
    val items: ImmutableList<WeekDayDish> = persistentListOf(),
) : VMState
