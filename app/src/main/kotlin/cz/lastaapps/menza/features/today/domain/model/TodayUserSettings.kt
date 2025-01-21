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

package cz.lastaapps.menza.features.today.domain.model

import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Unset

internal data class TodayUserSettings(
    val dishListMode: DishListMode? = null,
    val useOliverRow: Boolean = false,
    val priceType: PriceType = Unset,
    val downloadOnMetered: Boolean = false,
    val language: DataLanguage = DataLanguage.Czech,
    val imageScale: Float = 1f,
    val isDishListModeChosen: Boolean = true,
)
