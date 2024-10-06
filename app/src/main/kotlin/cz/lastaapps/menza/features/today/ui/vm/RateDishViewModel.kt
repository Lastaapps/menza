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
import cz.lastaapps.api.core.domain.model.RatingCategory
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

internal class RateDishViewModel(
    vmContext: VMContext,
) : StateViewModel<RatingState>(RatingState(), vmContext),
    ErrorHolder {
    fun onStar(
        category: RatingCategory,
        stars: Int,
    ) = updateState { copy(rating = rating.put(category, stars)) }

    fun submit() =
        launchVM {
            withLoading({ copy(submitting = it) }) {
                updateState { copy(error = null) }
                delay(0.5.seconds)
                updateState { copy(isSubmitted = true) }
            }
        }

    fun dismissDone() = updateState { RatingState() }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() = updateState { copy(error = null) }
}

internal data class RatingState(
    val rating: PersistentMap<RatingCategory, Int> =
        RatingCategory.entries
            .associateWith { 0 }
            .toPersistentMap(),
    val submitting: Boolean = false,
    val isSubmitted: Boolean = false,
    val error: DomainError? = null,
) : VMState {
    val isValid: Boolean
        get() = rating.values.all { it in 1..5 }
}
