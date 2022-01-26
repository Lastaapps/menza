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

package cz.lastaapps.menza.ui.dests.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun FullReloadDialog(
    shown: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    if (shown)
        Dialog(onDismissRequest = onDismissRequest) {
            Surface() {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Full Reload", style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "This operation clear local data like menza's names, location, contacts, etc. " +
                                "No settings are reset as result of this operation. " +
                                "Make sure you are connected to network, these data are required by the app to function correctly. " +
                                "App will be closed and the data will be downloaded on next app start."
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismissRequest) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            onConfirm()
                            onDismissRequest()
                        }) {
                            Text("Close app and reload")
                        }
                    }
                }
            }
        }
}