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

package cz.lastaapps.menza.features.today.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.rating.RatingCategories
import cz.lastaapps.api.core.domain.model.rating.RatingCategory
import cz.lastaapps.api.rating.domain.model.UserRating
import cz.lastaapps.api.rating.domain.usecase.RateDishUC
import cz.lastaapps.core.data.AppInfoProvider
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlin.random.Random

internal class RateDishViewModel(
    vmContext: VMContext,
    private val dishForRating: DishOriginDescriptor,
    private val rateDishUC: RateDishUC,
    provider: AppInfoProvider,
) : StateViewModel<RatingState>(RatingState(provider.isDebug()), vmContext),
    ErrorHolder {
    fun onStar(
        category: RatingCategory,
        stars: Int,
    ) = updateState { copy(rating = rating.put(category, stars)) }

    fun submit() =
        launchVM {
            withLoading({ copy(submitting = it) }) {
                updateState { copy(error = null) }
                val res =
                    rateDishUC(
                        UserRating(
                            dishForRating,
                            it.toDomain(),
                        ),
                    )
                updateState {
                    when (res) {
                        is Left -> copy(error = res.value)
                        is Right -> copy(isSubmitted = true)
                    }
                }
            }
        }

    fun dismissDone() = updateState { RatingState() }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() = updateState { copy(error = null) }
}

internal data class RatingState(
    val isDebug: Boolean = false,
    val rating: PersistentMap<RatingCategory, Int> =
        RatingCategory.entries
            .associateWith { if (isDebug) Random.nextInt(1, 5) else 0 }
            .toPersistentMap(),
    val submitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: DomainError? = null,
) : VMState {
    val isValid: Boolean
        get() = rating.values.all { it in 1..5 }

    fun toDomain() =
        RatingCategories(
            taste = rating[RatingCategory.TASTE]!!.toFloat(),
            portionSize = rating[RatingCategory.PORTION_SIZE]!!.toFloat(),
            worthiness = rating[RatingCategory.WORTHINESS]!!.toFloat(),
        )
}
