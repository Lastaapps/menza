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

package cz.lastaapps.menza.features.settings.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.components.MenzaDialog

@Composable
fun FullReloadDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    MenzaDialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.settings_reload_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(stringResource(R.string.settings_reload_text))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(R.string.settings_reload_cancel))
                }
                Button(onClick = {
                    onConfirm()
                    onDismissRequest()
                }) {
                    Text(stringResource(R.string.settings_reload_ok))
                }
            }
        }
    }
}