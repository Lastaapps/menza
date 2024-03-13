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

package cz.lastaapps.menza.features.today.domain.usecase

import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.usecase.GetDishLanguageUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetDishListModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImageScaleUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetImagesOnMeteredUC
import cz.lastaapps.menza.features.settings.domain.usecase.GetOliverRow
import cz.lastaapps.menza.features.settings.domain.usecase.GetPriceTypeUC
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

internal class GetTodayUserSettingsUC(
    context: UCContext,
    private val getOliverRowUC: GetOliverRow,
    private val getPriceTypeUC: GetPriceTypeUC,
    private val getImagesOnMeteredUC: GetImagesOnMeteredUC,
    private val getImageScaleUC: GetImageScaleUC,
    private val getDishLanguageUC: GetDishLanguageUC,
    private val getDishListModeUC: GetDishListModeUC,
) : UseCase(context) {
    operator fun invoke(): Flow<TodayUserSettings> = channelFlow {
        val state = MutableStateFlow(TodayUserSettings())

        fun updateState(block: TodayUserSettings.() -> TodayUserSettings) =
            state.update(block)

        getPriceTypeUC().onEach {
            updateState { copy(priceType = it) }
        }.launchIn(this)

        getImagesOnMeteredUC().onEach {
            updateState { copy(downloadOnMetered = it) }
        }.launchIn(this)

        getImageScaleUC().onEach {
            updateState { copy(imageScale = it) }
        }.launchIn(this)

        getDishLanguageUC().onEach {
            updateState { copy(language = it) }
        }.launchIn(this)

        getDishListModeUC().onEach {
            updateState { copy(dishListMode = it) }
        }.launchIn(this)

        getOliverRowUC().onEach {
            updateState { copy(useOliverRow = it) }
        }.launchIn(this)

        state.collect { send(it) }
    }
}
