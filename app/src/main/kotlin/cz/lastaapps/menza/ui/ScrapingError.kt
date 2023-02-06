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

package cz.lastaapps.menza.ui

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport
import cz.lastaapps.storage.repo.MenzaScrapingError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow


@Composable
fun CollectErrors(snackbarHost: SnackbarHostState, channel: Channel<MenzaScrapingError>) {
    val context = LocalContext.current
    var errorToReport by remember { mutableStateOf<MenzaScrapingError?>(null) }

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
    error: MenzaScrapingError,
    onReported: (MenzaScrapingError) -> Unit,
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

fun MenzaScrapingError.toMessage(context: Context): String {
    val errorMessage = throwable?.message?.let { "\n$it" } ?: ""
    return when (this) {
        MenzaScrapingError.WeekNotSupported -> cz.lastaapps.menza.R.string.error_week_not_supported
        is MenzaScrapingError.ParsingError -> cz.lastaapps.menza.R.string.error_parsing
        is MenzaScrapingError.UnknownConnectionError -> cz.lastaapps.menza.R.string.error_unknown_connection_error
        is MenzaScrapingError.ConnectionClosed -> cz.lastaapps.menza.R.string.error_connection_closed
        is MenzaScrapingError.FailedToConnect -> cz.lastaapps.menza.R.string.error_failed_to_connect
        is MenzaScrapingError.NoInternet -> cz.lastaapps.menza.R.string.error_no_internet
        is MenzaScrapingError.Timeout -> cz.lastaapps.menza.R.string.error_timeout
    }.let { context.getString(it) }.let {
        if (showMessage) it + errorMessage else it
    }
}
