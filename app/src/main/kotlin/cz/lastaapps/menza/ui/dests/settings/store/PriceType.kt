/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.settings.store

import androidx.datastore.preferences.core.intPreferencesKey
import cz.lastaapps.entity.common.Price
import cz.lastaapps.entity.day.Dish
import kotlinx.coroutines.flow.StateFlow

sealed class PriceType(val id: Int) {
    object Unset : PriceType(-1)
    object Discounted : PriceType(0)
    object Normal : PriceType(1)
}

private val priceTypeKey = intPreferencesKey("priceType")
val SettingsStore.priceType: StateFlow<PriceType>
    get() = data.mapState {
        when (it[priceTypeKey]) {
            PriceType.Discounted.id -> PriceType.Discounted
            PriceType.Normal.id -> PriceType.Normal
            else -> PriceType.Unset
        }
    }

suspend fun SettingsStore.setPriceType(priceType: PriceType) {
    edit { it[priceTypeKey] = priceType.id }
}

fun Dish.getPrice(type: PriceType): Price {
    return when (type) {
        PriceType.Discounted -> this.priceStudent
        else -> this.priceNormal
    }
}

