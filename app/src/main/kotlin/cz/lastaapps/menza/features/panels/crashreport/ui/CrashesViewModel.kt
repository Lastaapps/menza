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

package cz.lastaapps.menza.features.panels.crashreport.ui

import androidx.lifecycle.viewModelScope
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.crash.CrashDatabase
import cz.lastaapps.crash.entity.Crash
import cz.lastaapps.crash.entity.ErrorSeverity
import cz.lastaapps.crash.entity.ReportState
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel.State
import java.time.ZonedDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class CrashesViewModel(
    context: VMContext,
    private val database: CrashDatabase,
) : StateViewModel<State>(State(), context) {

    fun makeReported(id: Long, state: ReportState) {
        viewModelScope.launch {
            database.crashQueries.updateReported(state, id)
        }
    }

    init {
        launchVM {
            database.crashQueries.getCrashes { id: Long, date: ZonedDateTime, severity: ErrorSeverity, message: String?, trace: String, reported: ReportState ->
                id to Crash(date, severity, message, trace, reported)
            }
                .asFlow()
                .mapToList(coroutineContext)
                .collectLatest {
                    updateState {
                        copy(
                            errors = it.toImmutableList(),
                            hasErrors = it.isNotEmpty(),
                        )
                    }
                }
        }
        launchVM {
            database.crashQueries.getUnreported { id: Long, date: ZonedDateTime, severity: ErrorSeverity, message: String?, trace: String, reported: ReportState ->
                id to Crash(date, severity, message, trace, reported)
            }
                .asFlow()
                .mapToList(coroutineContext)
                .collectLatest {
                    updateState {
                        copy(unreported = it.toImmutableList())
                    }
                }
        }
        launchVM {
            database.crashQueries.hasUnreported().asFlow().mapToOne(coroutineContext)
                .collectLatest {
                    updateState {
                        copy(hasUnreported = it > 0)
                    }
                }
        }
    }

    data class State(
        val errors: ImmutableList<Pair<Long, Crash>> = persistentListOf(),
        val unreported: ImmutableList<Pair<Long, Crash>> = persistentListOf(),
        val hasErrors: Boolean = false,
        val hasUnreported: Boolean = false,
    ) : VMState
}
