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

package cz.lastaapps.menza.ui

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport
import cz.lastaapps.storage.repo.MenzaError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectErrors(snackbarHost: SnackbarHostState, channel: Channel<MenzaError>) {
    val context = LocalContext.current
    var errorToReport by remember { mutableStateOf<MenzaError?>(null) }

    LaunchedEffect(Unit) {
        channel.receiveAsFlow().collect { error ->
            handleError(context, snackbarHost, error) { errorToReport = it }
        }
    }

    ReportDialog(
        errorToReport != null, onDismissRequest = { errorToReport = null },
    ) {
        sendReport(context, it, errorToReport?.throwable)
    }
}

private suspend fun handleError(
    context: Context,
    snackbarHost: SnackbarHostState,
    error: MenzaError,
    onReported: (MenzaError) -> Unit
) {
    if (error.isReportable) {
        val result = snackbarHost.showSnackbar(
            message = error.toMessage(context),
            actionLabel = context.getString(cz.lastaapps.menza.R.string.error_button_report),
        )
        when (result) {
            SnackbarResult.Dismissed -> {}
            SnackbarResult.ActionPerformed -> {
                onReported(error)
            }
        }
    } else {
        snackbarHost.showSnackbar(error.toMessage(context))
    }
}

fun MenzaError.toMessage(context: Context): String {
    val errorMessage = throwable?.message?.let { "\n$it" } ?: ""
    return when (this) {
        MenzaError.WeekNotSupported -> cz.lastaapps.menza.R.string.error_week_not_supported
        is MenzaError.ParsingError -> cz.lastaapps.menza.R.string.error_parsing
        is MenzaError.UnknownConnectionError -> cz.lastaapps.menza.R.string.error_unknown_connection_error
        is MenzaError.ConnectionClosed -> cz.lastaapps.menza.R.string.error_connection_closed
        is MenzaError.FailedToConnect -> cz.lastaapps.menza.R.string.error_failed_to_connect
        is MenzaError.NoInternet -> cz.lastaapps.menza.R.string.error_no_internet
        is MenzaError.Timeout -> cz.lastaapps.menza.R.string.error_timeout
    }.let { context.getString(it) }.let {
        if (showMessage) it + errorMessage else it
    }
}