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

package cz.lastaapps.api.core.domain.sync

import com.squareup.sqldelight.Transacter

interface SyncProcessor {
    suspend fun runSync(
        list: Iterable<SyncJob<*, *>>,
        scope: List<(() -> Unit) -> Unit> = emptyList(),
        isForced: Boolean,
    ): SyncOutcome
}

suspend fun <T, R> SyncProcessor.runSync(
    job: SyncJob<T, R>,
    scope: List<(() -> Unit) -> Unit> = emptyList(),
    isForced: Boolean,
): SyncOutcome = runSync(listOf(job), scope, isForced)

suspend fun <T, R> SyncProcessor.runSync(
    job: SyncJob<T, R>,
    db: Transacter,
    isForced: Boolean,
) = runSync(listOf(job), db, isForced)

suspend fun SyncProcessor.runSync(
    list: Iterable<SyncJob<*, *>>,
    db: Transacter,
    isForced: Boolean,
): SyncOutcome = runSync(list, listOf { db.transaction { it() } }, isForced)
