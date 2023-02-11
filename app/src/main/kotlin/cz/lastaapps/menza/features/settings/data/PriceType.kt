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

package cz.lastaapps.menza.features.settings.data

import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val priceTypeKey = intPreferencesKey("priceType")
internal val SettingsStore.priceType: Flow<PriceType>
    get() = data.map {
        when (it[priceTypeKey]) {
            PriceType.Discounted.id -> PriceType.Discounted
            PriceType.Normal.id -> PriceType.Normal
            else -> PriceType.Unset
        }
    }

internal suspend fun SettingsStore.setPriceType(priceType: PriceType) {
    edit { it[priceTypeKey] = priceType.id }
}
