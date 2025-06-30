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

package cz.lastaapps.menza.features.today.domain.usecase

import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.usecase.settings.GetAppSettingsUC
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class GetTodayUserSettingsUC(
    context: UCContext,
    private val getAppSettingsUC: GetAppSettingsUC,
) : UseCase(context) {
    operator fun invoke(): Flow<TodayUserSettings> =
        getAppSettingsUC()
            .map {
                TodayUserSettings(
                    priceType = it.priceType,
                    currency = it.currency,
                    downloadOnMetered = it.imagesOnMetered,
                    imageScale = it.imageScale,
                    language = it.dataLanguage,
                    dishListMode = it.todayViewMode,
                    useOliverRow = it.useOliverRows,
                    isDishListModeChosen = it.isDishListModeChosen,
                )
            }.distinctUntilChanged()
}
