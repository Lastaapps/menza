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

package cz.lastaapps.core.util

import arrow.typeclasses.Monoid
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow


class FlowListMonoid<T> : Monoid<Flow<List<T>>> {
    override fun append(
        a: Flow<List<T>>,
        b: Flow<List<T>>,
    ): Flow<List<T>> = combine(a, b) { x, y -> (x as PersistentList<T>).addAll(y) }

    override fun empty(): Flow<List<T>> = flow { emit(persistentListOf()) }
}

fun <T> persistentListFlow() = flow<PersistentList<T>> { emit(persistentListOf()) }
