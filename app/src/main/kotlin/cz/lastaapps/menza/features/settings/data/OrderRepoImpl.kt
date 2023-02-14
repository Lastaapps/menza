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

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.core.util.persistentListFlow
import cz.lastaapps.menza.features.settings.data.datasource.OrderDataSource
import cz.lastaapps.menza.features.settings.domain.OrderRepo
import cz.lastaapps.menza.features.settings.domain.model.MenzaOrder
import kotlin.math.max
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


internal class OrderRepoImpl(
    private val source: OrderDataSource,
) : OrderRepo {

    private val lock = Mutex()

    override suspend fun initFromIfNeeded(list: List<Pair<MenzaType, Boolean>>) = lock.withLock {
        var (newVisible, newHidden) = getHighestKeys()
        list.forEach { (menza, important) ->
            source.getMenzaOrder(toKey(menza)) ?: run {
                source.putMenzaOrder(
                    toKey(menza),
                    MenzaOrder(
                        order = if (important) ++newVisible else ++newHidden,
                        visible = important,
                    )
                )
            }
        }
    }

    override suspend fun toggleVisible(menza: MenzaType) = lock.withLock {
        val (newVisible, newHidden) = getHighestKeys()
        val current = source.getMenzaOrder(toKey(menza)) ?: return

        if (current.visible) {
            MenzaOrder(newHidden + 1, false)
        } else {
            MenzaOrder(newVisible + 1, true)
        }.let { newOrder ->
            source.putMenzaOrder(toKey(menza), newOrder)
        }
    }

    override suspend fun switch(m1: MenzaType, m2: MenzaType) = lock.withLock {
        val o1 = source.getMenzaOrder(toKey(m1)) ?: return
        val o2 = source.getMenzaOrder(toKey(m2)) ?: return
        source.putMenzaOrder(toKey(m1), o2)
        source.putMenzaOrder(toKey(m2), o1)
    }

    override suspend fun updateOrder(list: List<Pair<MenzaType, Boolean>>) = lock.withLock {
        list.forEachIndexed { index, (menza, visible) ->
            source.putMenzaOrder(toKey(menza), MenzaOrder(index, visible))
        }
    }

    override fun getOrderFor(list: List<MenzaType>): Flow<List<Pair<MenzaType, MenzaOrder>>> =
        list
            .map {
                source.getMenzaOrderFlow(toKey(it))
            }
            .fold(persistentListFlow<MenzaOrder>()) { acu, item ->
                combine(acu, item) { a, i -> a.add(i) }
            }
            .onEach { lock.withLock {} }
            .mapLatest { data ->
                data.zip(list) { o, m -> m to o }
            }.map { data ->
                data.sortedBy { it.second }
            }

    private suspend fun getHighestKeys(): Pair<Int, Int> {
        var visibleCount = 0
        var hiddenCount = 0

        source.forEach { order ->
            order?.let { (order, visible) ->
                if (visible) {
                    visibleCount = max(visibleCount, order)
                } else {
                    hiddenCount = max(hiddenCount, order)
                }
            }
        }
        return visibleCount to hiddenCount
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun toKey(menza: MenzaType): String = menza.id

    override fun isFromTop(): Flow<Boolean> = source.isFromTop()

    override suspend fun setFromTop(fromTop: Boolean) = source.setFromTop(fromTop)
}
