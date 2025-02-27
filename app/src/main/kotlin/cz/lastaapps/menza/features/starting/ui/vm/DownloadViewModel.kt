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

package cz.lastaapps.menza.features.starting.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.features.starting.domain.model.DownloadProgress
import cz.lastaapps.menza.features.starting.domain.usecase.CheckDataDownloadNeededUC
import cz.lastaapps.menza.features.starting.domain.usecase.DownloadInitDataUC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.Duration.Companion.seconds

internal class DownloadViewModel(
    private val checkDownloadNeeded: CheckDataDownloadNeededUC,
    private val downloadData: DownloadInitDataUC,
    context: VMContext,
) : StateViewModel<DownloadDataState>(DownloadDataState(), context),
    ErrorHolder {
    private val log = localLogger()

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        log.i { "Appeared" }

        checkDownloadNeeded()
            .onEach {
                when (it) {
                    true -> {
                        log.i { "No data, starting" }
                        updateState { copy(isReady = true) }
                        startDownload()
                    }

                    false -> {
                        log.i { "Already has data" }
                        updateState {
                            copy(
                                isReady = true,
                                isDone = true,
                                downloadProgress = DownloadProgress.DONE,
                            )
                        }
                    }
                }
            }.launchIn(scope)
    }

    private fun startDownload() =
        launchVM {
            updateState { copy(isLoading = true) }

            log.i { "Starting download" }

            downloadData().collectLatest { res ->
                log.i { "Download progress: $res" }

                when (res) {
                    is Left ->
                        updateState {
                            log.i { "Setting an error" }
                            copy(
                                error = res.value,
                                isLoading = false,
                            )
                        }

                    is Right -> {
                        updateState { copy(downloadProgress = res.value) }

                        if (res.value == DownloadProgress.DONE) {
                            delay(1.seconds)
                            updateState { copy(isDone = true, isLoading = false) }
                        }
                    }
                }
            }
        }

    fun retry() =
        launchVM {
            log.i { "Retrying" }
            startDownload()
        }

    fun dismissDone() = updateState { copy(isDone = false) }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() =
        updateState {
            log.i { "Clearing error" }
            copy(error = null)
        }
}

internal data class DownloadDataState(
    val isReady: Boolean = false,
    val isLoading: Boolean = false,
    val downloadProgress: DownloadProgress = DownloadProgress.INIT,
    val error: DomainError? = null,
    val isDone: Boolean = false,
) : VMState
