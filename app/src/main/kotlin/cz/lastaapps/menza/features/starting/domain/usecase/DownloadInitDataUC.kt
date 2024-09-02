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

package cz.lastaapps.menza.features.starting.domain.usecase

import arrow.core.left
import arrow.core.right
import cz.lastaapps.api.main.domain.usecase.SyncAllInfoUC
import cz.lastaapps.api.main.domain.usecase.SyncMenzaListUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.starting.domain.model.DownloadProgress
import kotlinx.coroutines.flow.flow

internal class DownloadInitDataUC(
    context: UCContext,
    private val syncMenzaListUC: SyncMenzaListUC,
    private val syncInfoUC: SyncAllInfoUC,
) : UseCase(context) {
    private val log = localLogger()

    suspend operator fun invoke() =
        flow {
            emit(DownloadProgress.INIT.right())

            emit(DownloadProgress.MENZA_LIST.right())
            log.i { "Starting menza download" }

            syncMenzaListUC(isForced = true, all = true)
                .onLeft {
                    emit(it.left())
                    return@flow
                }

            emit(DownloadProgress.INFO.right())
            log.i { "Starting info download" }

            syncInfoUC(isForced = true)
                .let { resPair ->
                    resPair.first.firstOrNull()?.let { error ->
                        log.i { "Emitting info error: $error" }
                        emit(error.left())
                    }
                }

            log.i { "Done" }
            emit(DownloadProgress.DONE.right())
        }
}
