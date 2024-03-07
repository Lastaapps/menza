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

package cz.lastaapps.api.buffet.data.repo

import arrow.core.right
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FEL
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FS
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart


internal object MenzaFSRepoImpl : MenzaRepo {
    private val log = localLogger()

    override val isReady: Flow<Boolean> = MutableStateFlow(true)
        .onEach { log.i { "Is ready: $it" } }

    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        @Suppress("SpellCheckingInspection")
        persistentListOf(
            Menza(
                type = FS,
                name = "Bufet FS",
                isOpened = true,
                supportsDaily = true,
                supportsWeekly = true,
                isExperimental = true,
                videoLinks = persistentListOf(),
            ),
        ).let { emit(it) }
    }
        .onEach { log.i { "Menza produced: ${it.size}" } }
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        SyncResult.Skipped.right()
    }
}

internal object MenzaFELRepoImpl : MenzaRepo {
    private val log = localLogger()

    override val isReady: Flow<Boolean> = MutableStateFlow(true)
        .onEach { log.i { "Is ready: $it" } }

    override fun getData(): Flow<ImmutableList<Menza>> = flow {
        @Suppress("SpellCheckingInspection")
        persistentListOf(
            Menza(
                type = FEL,
                name = "Bufet FEL",
                isOpened = true,
                supportsDaily = true,
                supportsWeekly = true,
                isExperimental = true,
                videoLinks = persistentListOf(),
            ),
        ).let { emit(it) }
    }
        .onEach { log.i { "Menza produced: ${it.size}" } }
        .onStart { log.i { "Starting collection" } }
        .onCompletion { log.i { "Completed collection" } }

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Starting sync (f: $isForced)" }
        SyncResult.Skipped.right()
    }
}
