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
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishCategory
import cz.lastaapps.api.rating.domain.usecase.GetDishRatingsUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class GetTodayDishListUC internal constructor(
    context: UCContext,
    private val getRawDishList: GetTodayRawDishListUC,
    private val getDishRatingsUC: GetDishRatingsUC,
) : UseCase(context),
    KoinComponent {
    private val cache =
        FlowParametrizedCache<ImmutableList<DishCategory>, MenzaType>()

    suspend operator fun invoke(menza: Menza): Flow<ImmutableList<DishCategory>> = invoke(menza.type)

    suspend operator fun invoke(menza: MenzaType): Flow<ImmutableList<DishCategory>> =
        launch {
            cache(menza) { menza ->
                val dishFlow =
                    getRawDishList(menza).map {
                        it
                            .map { category ->
                                val newDishList = category.dishList.filter { dish -> dish.isActive }

                                if (newDishList == category.dishList) {
                                    category
                                } else {
                                    category.copy(dishList = newDishList.toImmutableList())
                                }
                            }.toImmutableList()
                    }

                val ratingsFlow = getDishRatingsUC(menza)

                combine(
                    dishFlow.distinctUntilChanged(),
                    ratingsFlow.distinctUntilChanged(),
                ) { dishList, ratings ->
                    dishList
                        .map { category ->
                            category.copy(
                                dishList =
                                    category.dishList
                                        .map { dish ->
                                            ratings[dish.id]?.let {
                                                dish.copy(rating = it)
                                            } ?: dish
                                        }.toImmutableList(),
                            )
                        }.toImmutableList()
                }
            }
        }
}
