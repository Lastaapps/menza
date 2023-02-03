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

package cz.lastaapps.api.main.data

import arrow.core.Either
import arrow.core.fold
import arrow.core.right
import arrow.fx.coroutines.parMap
import arrow.typeclasses.Monoid
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.core.domain.error.MenzaError
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent

internal class MenzaMasterRepoImpl(
    private val sources: List<MenzaRepo>,
) : MenzaRepo, KoinComponent {

    private val monoid = object : Monoid<Flow<PersistentList<Menza>>> {
        override fun append(
            a: Flow<PersistentList<Menza>>,
            b: Flow<PersistentList<Menza>>,
        ): Flow<PersistentList<Menza>> = combine(a, b) { x, y -> x.addAll(y) }

        override fun empty(): Flow<PersistentList<Menza>> = flow { emit(persistentListOf()) }
    }

    override fun getData(): Flow<ImmutableList<Menza>> =
        sources
            .map { repo ->
                repo.getData().map { it.toPersistentList() }
            }
            .fold(monoid)

    override suspend fun sync(isForced: Boolean): SyncOutcome =
        sources.parMap {
            it.sync()
        }.let { res ->
            res.firstOrNull { it is Either.Left<MenzaError> }?.let { return it }
            val updated = res.map { (it as Either.Right<SyncResult>).value }
            updated.firstOrNull { it is SyncResult.Problem }?.let { return it.right() }
            updated.firstOrNull { it is SyncResult.Updated }?.let { return it.right() }
            return SyncResult.Skipped.right()
        }
}
