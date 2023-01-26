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

package cz.lastaapps.menza.api.agata.domain.sync

import arrow.core.Nel
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.MenzaError

typealias SyncOutcome = Outcome<SyncResult>

interface SyncResult {
    data object Updated : SyncResult
    data object Skipped : SyncResult
    data object Unavailable : SyncResult

    @JvmInline
    value class Problem(val errors: Nel<MenzaError>) : SyncResult
}
