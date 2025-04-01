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

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.left
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.sync.SyncResult.Updated
import cz.lastaapps.api.core.domain.sync.sync
import cz.lastaapps.api.rating.domain.usecase.SyncDishRatingsUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.domain.error.ApiError.RatingError
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class SyncTodayDishListUC(
    context: UCContext,
    private val getRequestParamsUC: GetRequestParamsUC,
    private val syncDishRatingsUC: SyncDishRatingsUC,
) : UseCase(context),
    KoinComponent {
    suspend operator fun invoke(
        menza: Menza,
        isForced: Boolean,
    ) = launch {
        coroutineScope {
            val syncDish =
                async {
                    get<TodayDishRepo> { parametersOf(menza.type) }.sync(
                        getRequestParamsUC(),
                        isForced = isForced,
                    )
                }
            val syncRating =
                async {
                    syncDishRatingsUC(menza.type, isForced)
                }
            val dish = syncDish.await()
            val rating = syncRating.await()

            when (dish) {
                is Left -> dish
                is Right -> {
                    when (rating) {
                        is Left -> RatingError.wrap(rating.value).left()
                        is Right -> {
                            when (dish.value) {
                                Updated -> rating
                                else -> dish
                            }
                        }
                    }
                }
            }
        }
    }
}
