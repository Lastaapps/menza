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

package cz.lastaapps.menza.settings.domain.usecase.menzaorder

import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.settings.domain.OrderRepo

class UpdateMenzaOrderUC internal constructor(
    context: UCContext,
    private val repo: OrderRepo,
) : UseCase(context) {
    suspend operator fun invoke(list: List<Pair<Menza, Boolean>>) = launch {
        repo.updateOrder(list.map { (menza, visible) ->
            menza.type to visible
        })
    }
}
