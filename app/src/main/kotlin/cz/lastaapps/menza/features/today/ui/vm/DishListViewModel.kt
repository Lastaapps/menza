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

package cz.lastaapps.menza.features.today.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import cz.lastaapps.api.core.domain.model.common.DishCategory
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.main.domain.usecase.GetTodayDishListUC
import cz.lastaapps.api.main.domain.usecase.OpenMenuUC
import cz.lastaapps.api.main.domain.usecase.SyncTodayDishListUC
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.usecase.IsOnMeteredUC
import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.menza.features.main.domain.usecase.GetSelectedMenzaUC
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset
import cz.lastaapps.menza.features.settings.domain.model.ShowCzech
import cz.lastaapps.menza.features.settings.domain.usecase.GetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetPriceTypeUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetShowCzechUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

internal class DishListViewModel(
    context: VMContext,
    private val getSelectedMenza: GetSelectedMenzaUC,
    private val getTodayDishList: GetTodayDishListUC,
    private val syncTodayDishList: SyncTodayDishListUC,
    private val getPriceType: GetPriceTypeUC,
    private val getImagesOnMetered: GetImagesOnMeteredUC,
    private val getImageScale: GetImageScaleUC,
    private val getShowCzech: GetShowCzechUC,
    private val isOnMetered: IsOnMeteredUC,
    private val openMenuLink: OpenMenuUC,
) : StateViewModel<DishListState>(DishListState(), context), Appearing, ErrorHolder {
    override var hasAppeared: Boolean = false

    companion object {
        private val log = logging()
    }

    override fun onAppeared() = launch {
        launch {
            getSelectedMenza().collectLatest {
                log.i { "Registered a new: $it" }

                updateState {
                    copy(
                        selectedMenza = it,
                        items = persistentListOf(),
                    )
                }
                syncJob?.cancel()
                if (it != null) {
                    coroutineScope {
                        this.launch {
                            load(it, false)
                        }
                        getTodayDishList(it).collectLatest { items ->
                            updateState { copy(items = items) }
                        }
                    }
                }
            }
        }

        getPriceType().onEach {
            updateState { copy(priceType = it) }
        }.launchInVM()

        getImagesOnMetered().onEach {
            updateState { copy(downloadOnMetered = it) }
        }.launchInVM()

        getImageScale().onEach {
            updateState { copy(imageScale = it) }
        }.launchInVM()

        getShowCzech().onEach {
            updateState { copy(showCzech = it) }
        }.launchInVM()

        isOnMetered().onEach {
            updateState { copy(isOnMetered = it) }
        }.launchInVM()
    }

    private var syncJob: Job? = null
    fun reload() {
        if (lastState().isLoading) return
        syncJob = launchJob {
            lastState().selectedMenza?.let {
                load(it, true)
            }
        }
    }

    fun openWebMenu() = launch {
        lastState().selectedMenza?.let { openMenuLink(it) }
    }

    private suspend fun load(menza: Menza, isForced: Boolean) {
        withLoading({ copy(isLoading = it) }) {
            when (val res = syncTodayDishList(menza, isForced = isForced)) {
                is Left -> updateState { copy(error = res.value) }
                is Right -> {}
            }
        }
    }

    @Composable
    override fun getError(): MenzaError? = flowState.value.error
    override fun dismissError() = updateState { copy(error = null) }
}

internal data class DishListState(
    val isLoading: Boolean = false,
    val error: MenzaError? = null,
    val selectedMenza: Menza? = null,
    val items: ImmutableList<DishCategory> = persistentListOf(),
    val priceType: PriceType = Unset,
    val downloadOnMetered: Boolean = false,
    val showCzech: ShowCzech = ShowCzech(true),
    val imageScale: Float = 1f,
    val isOnMetered: Boolean = false,
)
