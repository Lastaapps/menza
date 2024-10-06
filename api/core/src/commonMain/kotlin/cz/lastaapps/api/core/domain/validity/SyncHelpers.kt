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

package cz.lastaapps.api.core.domain.validity

import arrow.core.right
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant
import kotlin.time.Duration

suspend inline fun ValidityChecker.withCheckRecent(
    key: ValidityKey,
    forced: Boolean,
    block: () -> SyncOutcome,
): SyncOutcome = withCheck(key, forced, { isRecent(key).first() }, block)

suspend inline fun ValidityChecker.withCheckSince(
    key: ValidityKey,
    forced: Boolean,
    date: Flow<Instant>,
    block: () -> SyncOutcome,
): SyncOutcome = withCheck(key, forced, { isUpdatedSince(key, date).first() }, block)

suspend inline fun ValidityChecker.withCheckSince(
    key: ValidityKey,
    forced: Boolean,
    duration: Duration,
    block: () -> SyncOutcome,
): SyncOutcome = withCheck(key, forced, { isUpdatedSince(key, duration).first() }, block)

suspend inline fun ValidityChecker.withCheck(
    key: ValidityKey,
    forced: Boolean,
    canSkip: () -> Boolean,
    block: () -> SyncOutcome,
): SyncOutcome =
    if (canSkip() && !forced) {
        SyncResult.Skipped.right()
    } else {
        block().onRight {
            onDataUpdated(key)
        }
    }
