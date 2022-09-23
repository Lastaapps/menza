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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.crash.entity.Crash
import cz.lastaapps.crash.entity.ErrorSeverity
import cz.lastaapps.crash.entity.ReportState
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport
import kotlinx.collections.immutable.ImmutableList
import java.time.format.DateTimeFormatter

@Composable
fun CrashesDialog(viewModel: CrashesViewModel, onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest) {
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            CrashesList(viewModel, Modifier.padding(16.dp))
        }
    }
}

@Composable
fun CrashesList(
    viewModel: CrashesViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {

        if (BuildConfig.DEBUG) {
            Button(
                onClick = { throw RuntimeException("London bridge is falling down") },
                Modifier.align(Alignment.CenterHorizontally),
            ) { Text(stringResource(R.string.crash_do)) }
        }

        val crashes = viewModel.errors.collectAsState().value
        if (crashes.isEmpty()) {
            NoContent()
        } else {
            var selectedItem by remember { mutableStateOf<Pair<Long, Crash>?>(null) }

            val context = LocalContext.current
            ReportDialog(
                shown = selectedItem != null,
                onDismissRequest = { selectedItem = null },
                onModeSelected = { mode ->
                    selectedItem?.let { crash ->
                        viewModel.makeReported(crash.first)
                        sendReport(context, mode, crash.second)
                    }
                }
            )

            Content(crashes) {
                selectedItem = it
            }
        }
    }
}

@Composable
private fun NoContent() {
    Text(
        stringResource(R.string.crash_none_title),
        style = MaterialTheme.typography.headlineMedium
    )
    Text(stringResource(R.string.crash_none_subtitle))
    Text(stringResource(R.string.crash_none_subsubtitle))
}

@Composable
private fun Content(
    crashes: ImmutableList<Pair<Long, Crash>>,
    onItemSelected: (Pair<Long, Crash>) -> Unit
) {
    Text(
        stringResource(R.string.crash_title),
        style = MaterialTheme.typography.headlineMedium
    )
    Text(stringResource(R.string.crash_subtitle))

    Surface(shape = MaterialTheme.shapes.large) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(crashes, key = { it.first }) {
                CrashItem(it.second) {
                    onItemSelected(it)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CrashItem(crash: Crash, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            with(crash) {
                Text(
                    message ?: stringResource(R.string.crash_message_unknown),
                    style = MaterialTheme.typography.titleLarge,
                )
                Column {
                    Text(
                        stringResource(
                            R.string.crash_date_title, crash.date.format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            )
                        )
                    )
                    Text(
                        stringResource(
                            R.string.crash_severity_title, when (crash.severity) {
                                ErrorSeverity.CRASH -> stringResource(R.string.crash_severity_crash)
                                ErrorSeverity.HANDLED -> stringResource(R.string.crash_severity_internal)
                            }
                        )
                    )
                    Text(
                        stringResource(
                            R.string.crash_status_title, when (crash.reported) {
                                ReportState.UNREPORTED -> stringResource(R.string.crash_status_unreported)
                                ReportState.DISMISSED -> stringResource(R.string.crash_status_dismissed)
                                ReportState.REPORTED -> stringResource(R.string.crash_status_reported)
                            }
                        )
                    )
                }
                Card {
                    Text(
                        crash.trace,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}