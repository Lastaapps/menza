/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.others.crashes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import cz.lastaapps.crash.CrashDatabase
import cz.lastaapps.crash.entity.Crash
import cz.lastaapps.crash.entity.ErrorSeverity
import cz.lastaapps.crash.entity.ReportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CrashesViewModel @Inject constructor(
    private val database: CrashDatabase,
) : ViewModel() {

    val errors: StateFlow<List<Pair<Long, Crash>>> get() = mErrors
    private val mErrors = MutableStateFlow<List<Pair<Long, Crash>>>(emptyList())

    val unreported: StateFlow<List<Pair<Long, Crash>>> get() = mUnreported
    private val mUnreported = MutableStateFlow<List<Pair<Long, Crash>>>(emptyList())

    val hasErrors: StateFlow<Boolean> get() = mHasErrors
    private val mHasErrors = MutableStateFlow(false)

    val hasUnreported: StateFlow<Boolean> get() = mHasErrors
    private val mHasUnreported = MutableStateFlow(false)

    fun makeReported(id: Long, state: ReportState = ReportState.REPORTED) {
        viewModelScope.launch {
            database.crashQueries.updateReported(state, id)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database.crashQueries.getCrashes() { id: Long, date: ZonedDateTime, severity: ErrorSeverity, message: String?, trace: String, reported: ReportState ->
                id to Crash(date, severity, message, trace, reported)
            }.asFlow().mapToList(coroutineContext).collectLatest {
                mErrors.emit(it)
                mHasErrors.emit(it.isNotEmpty())
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            database.crashQueries.getUnreported { id: Long, date: ZonedDateTime, severity: ErrorSeverity, message: String?, trace: String, reported: ReportState ->
                id to Crash(date, severity, message, trace, reported)
            }.asFlow().mapToList(coroutineContext).collectLatest {
                mUnreported.emit(it)
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            database.crashQueries.hasUnreported().asFlow().mapToOne(coroutineContext)
                .collectLatest {
                    mHasUnreported.emit(it > 0)
                }
        }
    }
}
