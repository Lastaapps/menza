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

package cz.lastaapps.api.core.domain.sync

import app.cash.sqldelight.Transacter

interface SyncProcessor<Params> {
    suspend fun runSync(
        list: Iterable<SyncJob<*, *, Params>>,
        scope: List<(() -> Unit) -> Unit> = emptyList(),
        params: Params,
        isForced: Boolean,
    ): SyncOutcome
}

suspend fun <T, R, Params> SyncProcessor<Params>.runSync(
    job: SyncJob<T, R, Params>,
    scope: List<(() -> Unit) -> Unit> = emptyList(),
    params: Params,
    isForced: Boolean,
): SyncOutcome = runSync(listOf(job), scope, params, isForced)

suspend fun <T, R, Params> SyncProcessor<Params>.runSync(
    job: SyncJob<T, R, Params>,
    db: Transacter,
    params: Params,
    isForced: Boolean,
) = runSync(listOf(job), db, params, isForced)

suspend fun <Params> SyncProcessor<Params>.runSync(
    list: Iterable<SyncJob<*, *, Params>>,
    db: Transacter,
    params: Params,
    isForced: Boolean,
): SyncOutcome = runSync(list, listOf { db.transaction { it() } }, params, isForced)
