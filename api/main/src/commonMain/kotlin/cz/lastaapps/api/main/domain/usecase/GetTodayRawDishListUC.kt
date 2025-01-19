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

package cz.lastaapps.api.main.domain.usecase

import cz.lastaapps.api.core.domain.FlowParametrizedCache
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.sync.getData
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

internal class GetTodayRawDishListUC internal constructor(
    context: UCContext,
    private val getRequestParamsUC: GetRequestParamsUC,
) : UseCase(context),
    KoinComponent {
    private val cache = FlowParametrizedCache<ImmutableList<DishCategory>, MenzaType>()

    suspend operator fun invoke(menza: MenzaType): Flow<ImmutableList<DishCategory>> =
        launch {
            cache(menza) { menza ->
                get<TodayDishRepo> { parametersOf(menza) }
                    .getData(getRequestParamsUC())
            }
        }
}
