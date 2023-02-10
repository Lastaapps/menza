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

@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)

package cz.lastaapps.menza.settings.data.datasource

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import cz.lastaapps.menza.settings.domain.model.MenzaOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

@JvmInline
internal value class OrderSettings(val settings: FlowSettings) {
    companion object {
        private val Context.store by preferencesDataStore("menza_order_store")

        fun create(context: Context) = OrderSettings(DataStoreSettings(context.store))
    }
}

internal interface OrderDataSource {
    suspend fun putMenzaOrder(key: String, order: MenzaOrder)

    suspend fun getMenzaOrder(key: String): MenzaOrder?

    fun getMenzaOrderFlow(key: String): Flow<MenzaOrder>

    suspend fun forEach(block: (MenzaOrder?) -> Unit)

    fun isFromTop(): Flow<Boolean>

    suspend fun setFromTop(fromTop: Boolean)
}

internal class OrderDataSourceImpl(
    orderSettings: OrderSettings,
) : OrderDataSource {
    private val settings = orderSettings.settings

    companion object {
        private const val orderPrefix = "order_"
        private const val visiblePrefix = "visible_"
        private const val internalPrefix = "internal_"

        private const val fromTopKey = internalPrefix + "from_top"

        private fun orderKey(key: String) = orderPrefix + key
        private fun visibleKey(key: String) = visiblePrefix + key

        private fun Set<String>.removePrefixes() =
            asSequence()
                .map { it.removePrefix(orderPrefix).removePrefix(visiblePrefix) }
                .filter { !it.startsWith(internalPrefix) }
                .toSet()
    }

    override suspend fun putMenzaOrder(key: String, order: MenzaOrder) {
        settings.putInt(orderKey(key), order.order)
        settings.putBoolean(visibleKey(key), order.visible)
    }

    override suspend fun getMenzaOrder(key: String): MenzaOrder? {
        return MenzaOrder(
            order = settings.getIntOrNull(orderKey(key)) ?: return null,
            visible = settings.getBooleanOrNull(visibleKey(key)) ?: return null
        )
    }

    override fun getMenzaOrderFlow(key: String): Flow<MenzaOrder> {
        val larges = MenzaOrder.largest
        return combine(
            settings.getIntFlow(orderKey(key), larges.order),
            settings.getBooleanFlow(visibleKey(key), larges.visible)
        ) { order, visible ->
            MenzaOrder(order = order, visible = visible)
        }
    }

    override suspend fun forEach(block: (MenzaOrder?) -> Unit) {
        settings.keys().removePrefixes()
            .forEach { key -> block(getMenzaOrder(key)) }
    }

    override fun isFromTop(): Flow<Boolean> =
        settings.getBooleanFlow(fromTopKey, true)

    override suspend fun setFromTop(fromTop: Boolean) {
        settings.putBoolean(fromTopKey, fromTop)
    }
}
