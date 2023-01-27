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

package cz.lastaapps.api.buffet.data.repo

import arrow.core.right
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal object MenzaFSRepoImpl : MenzaRepo {
    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        @Suppress("SpellCheckingInspection")
        persistentListOf(
            Menza(
                type = MenzaType.Buffet.FS,
                name = "Bufet FS",
                isImportant = true,
                isOpened = true,
            )
        ).let { emit(it) }
    }

    override suspend fun sync(): SyncOutcome = SyncResult.Skipped.right()
}

internal object MenzaFELRepoImpl : MenzaRepo {
    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        @Suppress("SpellCheckingInspection")
        persistentListOf(
            Menza(
                type = MenzaType.Buffet.FEL,
                name = "Bufet FEL",
                isImportant = true,
                isOpened = true,
            )
        ).let { emit(it) }
    }

    override suspend fun sync(): SyncOutcome = SyncResult.Skipped.right()
}
