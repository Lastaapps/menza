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

package cz.lastaapps.menza.features.panels.crashreport.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.crash.entity.ReportState
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.other.ui.dialog.ReportDialog
import cz.lastaapps.menza.features.other.ui.dialog.sendReport
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel.State

@Composable
internal fun CrashReport(
    state: State,
    makeReported: (id: Long, state: ReportState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val unreported = state.unreported.firstOrNull() ?: return

    Column(
        modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            stringResource(R.string.panel_crash_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            stringResource(
                R.string.panel_crash_subtitle,
                unreported.second.message ?: stringResource(R.string.panel_crash_unknown),
            ),
        )

        Row(Modifier.align(Alignment.End), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(
                onClick = {
                    makeReported(
                        unreported.first,
                        ReportState.DISMISSED,
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
                true,
                onDismissRequest = { reportShown = false },
                onModeSelected = { mode ->
                    makeReported(unreported.first, ReportState.REPORTED)
                    sendReport(context, mode, unreported.second)
                },
            )

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