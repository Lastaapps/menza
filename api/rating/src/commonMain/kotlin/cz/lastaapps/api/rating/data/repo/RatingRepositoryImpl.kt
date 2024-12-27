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

package cz.lastaapps.api.rating.data.repo

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.Rating
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult.Updated
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.api.rating.api.RatingAPI
import cz.lastaapps.api.rating.data.model.toDomain
import cz.lastaapps.api.rating.data.repo.RatingRepository.Params
import cz.lastaapps.api.rating.domain.model.UserRating
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

private typealias Entry = ImmutableMap<DishID, Rating>

internal class RatingRepositoryImpl(
    private val api: RatingAPI,
    private val checker: ValidityChecker,
) : RatingRepository {
    private val log = localLogger()

    private val state = MutableStateFlow<PersistentMap<MenzaType, Entry>>(persistentMapOf())
    private val validityKey = ValidityKey.rating()

    override fun getData(params: Params): Flow<ImmutableMap<DishID, Rating>> =
        run {
            state.map { it[params.menza] ?: persistentMapOf() }.distinctUntilChanged()
        }

    private fun updateValue(
        menza: MenzaType,
        data: Entry,
    ) {
        state.update { it.put(menza, data) }
    }

    override suspend fun rate(rating: UserRating): Outcome<Unit> =
        run {
            with(rating) {
                api.rate(
                    dish.menza,
                    dish.id,
                    rating.dish.name,
                    dish.language,
                    ratingCategories,
                )
            }.onRight {
                updateValue(rating.dish.menza, it.toDomain())
                checker.onDataUpdated(validityKey.withParams(Params(rating.dish.menza)))
            }.map {}
        }

    override suspend fun sync(
        params: Params,
        isForced: Boolean,
    ): SyncOutcome =
        run {
            log.i { "Starting sync (f: $isForced)" }
            checker.withCheckRecent(validityKey.withParams(params), isForced) {
                when (val res = api.getRatings(params.menza)) {
                    is Left -> res
                    is Right -> {
                        updateValue(params.menza, res.value.toDomain())
                        Updated.right()
                    }
                }
            }
        }

    private fun ValidityKey.withParams(params: Params) = withMenzaType(params.menza)
}
