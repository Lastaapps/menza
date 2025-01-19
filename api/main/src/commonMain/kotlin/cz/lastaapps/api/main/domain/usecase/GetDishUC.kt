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

import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.rating.domain.usecase.GetDishRatingsUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

class GetDishUC internal constructor(
    context: UCContext,
    private val getRawDishList: GetTodayRawDishListUC,
    private val getDishRatingsUC: GetDishRatingsUC,
) : UseCase(context),
    KoinComponent {
    suspend operator fun invoke(dish: Dish) = invoke(DishOriginDescriptor.from(dish))

    suspend operator fun invoke(dishOrigin: DishOriginDescriptor) =
        launch {
            val dishFlow =
                getRawDishList(dishOrigin.menza)
                    .map { categories ->
                        categories.forEach { category ->
                            category.dishList
                                .firstOrNull { dishOrigin.conforms(it) }
                                ?.let { return@map it }
                        }
                        null
                    }

            val ratingsFlow = getDishRatingsUC(dishOrigin.menza)

            combine(
                dishFlow.distinctUntilChanged(),
                ratingsFlow.distinctUntilChanged(),
            ) { dish, ratings ->
                dish?.let {
                    ratings[it.id]?.let { rating ->
                        dish.copy(rating = rating)
                    }
                } ?: dish
            }.distinctUntilChanged()
        }
}
