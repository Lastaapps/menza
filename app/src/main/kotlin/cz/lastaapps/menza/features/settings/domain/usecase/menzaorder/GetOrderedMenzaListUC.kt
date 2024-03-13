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

package cz.lastaapps.menza.features.settings.domain.usecase.menzaorder

import cz.lastaapps.api.main.domain.usecase.GetMenzaListUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.OrderRepo
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest

class GetOrderedMenzaListUC internal constructor(
    context: UCContext,
    private val getMenzaList: GetMenzaListUC,
    private val orderRepo: OrderRepo,
) : UseCase(context) {
    operator fun invoke() = channelFlow {
        getMenzaList().collectLatest { list ->
            orderRepo.initFromIfNeeded(
                list.map { menza ->
                    menza.type to (menza.supportsDaily || menza.supportsWeekly)
                },
            )

            orderRepo.getOrderFor(list.map { it.type }).collect { ordered ->
                ordered.map { (type, order) ->
                    list.first { menza -> menza.type == type } to order
                }
                    .toImmutableList()
                    .let { send(it) }
            }
        }
    }
}
