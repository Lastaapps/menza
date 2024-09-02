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

package cz.lastaapps.menza.features.settings.domain

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.menza.features.settings.domain.model.MenzaOrder
import kotlinx.coroutines.flow.Flow

interface OrderRepo {
    /**
     * Adds unknown systems at the end of the visible spectrum if they are considered important
     */
    suspend fun initFromIfNeeded(list: List<Pair<MenzaType, Boolean>>)

    suspend fun toggleVisible(menza: MenzaType)

    suspend fun switch(
        m1: MenzaType,
        m2: MenzaType,
    )

    suspend fun updateOrder(list: List<Pair<MenzaType, Boolean>>)

    fun getOrderFor(list: List<MenzaType>): Flow<List<Pair<MenzaType, MenzaOrder>>>

    fun isFromTop(): Flow<Boolean>

    suspend fun setFromTop(fromTop: Boolean)
}
