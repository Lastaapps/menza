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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.core.domain.error.MenzaError
import cz.lastaapps.core.domain.error.shouldBeReported
import cz.lastaapps.core.ui.text
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport


// TODO move later in the core module
// TODO test with new compose version if more errors can be shown in a row
@Composable
fun HandleError(holder: ErrorHolder, hostState: SnackbarHostState) =
    HandleError(
        error = holder.getError(),
        hostState = hostState,
        onDismiss = holder::dismissError,
    )

@Composable
fun HandleError(error: MenzaError?, hostState: SnackbarHostState, onDismiss: () -> Unit) {

    var toReport by remember { mutableStateOf<MenzaError?>(null) }

    val context = LocalContext.current
    LaunchedEffect(error, hostState, context) {
        error?.let {
            onDismiss()
            if (it.shouldBeReported) {
                val result = hostState.showSnackbar(
                    message = error.text(context),
                    actionLabel = context.getString(cz.lastaapps.menza.R.string.error_button_report),
                )
                when (result) {
                    SnackbarResult.Dismissed -> {}
                    SnackbarResult.ActionPerformed -> {
                        toReport = error
                    }
                }
            } else {
                hostState.showSnackbar(message = error.text(context))
            }
        }
    }

    toReport?.let { errorToReport ->
        ReportDialog(onDismissRequest = { toReport = null }) {
            sendReport(context, it, errorToReport.throwable)
        }
    }
}
