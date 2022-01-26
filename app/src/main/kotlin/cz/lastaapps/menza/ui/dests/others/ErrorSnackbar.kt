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

package cz.lastaapps.menza.ui.dests.others

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.storage.repo.MenzaError
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectErrors(snackbarHost: SnackbarHostState, channel: Channel<MenzaError>) {
    val context = LocalContext.current
    var errorToReport by remember { mutableStateOf<MenzaError?>(null) }
    LaunchedEffect(channel) {
        channel.consumeEach { error ->
            if (error.isReportable) {
                val result = snackbarHost.showSnackbar(
                    message = error.toMessage(),
                    actionLabel = "Report",
                )
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> {
                        errorToReport = error
                    }
                }
            } else {
                snackbarHost.showSnackbar(error.toMessage())
            }
        }
    }

    ReportDialog(
        errorToReport != null, onDismissRequest = { errorToReport = null },
    ) {
        sendReport(context, it, errorToReport?.throwable)
    }
}

fun MenzaError.toMessage(): String {
    val errorMessage = throwable?.message?.let { "\n$it" } ?: ""
    return when (this) {
        MenzaError.WeekNotSupported -> "This menza doesn't support Week menu"
        is MenzaError.UnknownConnectionError -> "Unknown connection error"
        is MenzaError.ParsingError -> "Internal app error"
        is MenzaError.ConnectionClosed -> "Connection closed"
        is MenzaError.FailedToConnect -> "Failed to connect to the server"
        is MenzaError.NoInternet -> "No Internet available"
        is MenzaError.Timeout -> "Connection timed out"
    }.let {
        if (showMessage) it + errorMessage else it
    }
}