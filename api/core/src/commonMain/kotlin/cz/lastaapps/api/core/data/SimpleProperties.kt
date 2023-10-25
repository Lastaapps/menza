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

package cz.lastaapps.api.core.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow

interface SimpleProperties {
    suspend fun setBalance(balance: Float?)

    fun getBalance(): Flow<Float?>
}

@OptIn(ExperimentalSettingsApi::class)
@JvmInline
internal value class SimplePropertiesImpl(
    private val properties: FlowSettings,
) : SimpleProperties {
    override suspend fun setBalance(balance: Float?) {
        balance?.let { properties.putFloat(KEY_BALANCE, balance) } ?: run {
            properties.remove(KEY_BALANCE)
        }
    }

    override fun getBalance(): Flow<Float?> = properties.getFloatOrNullFlow(KEY_BALANCE)

    companion object {
        private const val KEY_BALANCE = "balance"
    }
}
