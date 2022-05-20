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

package cz.lastaapps.menza.ui.dests.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.crash.entity.ReportState
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.crashes.CrashesViewModel
import cz.lastaapps.menza.ui.dests.others.sendReport

@Composable
fun crashReportState(crashesViewModel: CrashesViewModel): State<Boolean> {
    return crashesViewModel.hasUnreported.collectAsState()
}

@Composable
fun CrashReport(crashesViewModel: CrashesViewModel, modifier: Modifier = Modifier) {
    val unreported = crashesViewModel.unreported.collectAsState().value.firstOrNull() ?: return

    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.panel_crash_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            stringResource(
                R.string.panel_crash_subtitle,
                unreported.second.message ?: stringResource(R.string.panel_crash_unknown),
            )
        )

        Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = {
                    crashesViewModel.makeReported(
                        unreported.first,
                        ReportState.DISMISSED
                    )
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                ),
            ) { Text(stringResource(R.string.panel_crash_dismiss)) }

            val context = LocalContext.current
            var reportShown by remember { mutableStateOf(false) }

            ReportDialog(
                reportShown,
                onDismissRequest = { reportShown = false },
                onModeSelected = { mode ->
                    crashesViewModel.makeReported(unreported.first)
                    sendReport(context, mode, unreported.second)
                })

            Button(
                onClick = { reportShown = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
            ) { Text(stringResource(R.string.panel_crash_report)) }
        }
    }
}