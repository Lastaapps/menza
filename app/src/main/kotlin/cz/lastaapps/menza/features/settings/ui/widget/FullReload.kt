/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.settings.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.components.BaseDialog
import cz.lastaapps.menza.ui.theme.Padding

@Suppress("ktlint:compose:modifier-missing-check")
@Composable
fun FullReloadDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    BaseDialog(onDismissRequest = onDismissRequest) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Text(
                stringResource(R.string.settings_reload_title),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(stringResource(R.string.settings_reload_text))

            Button(onClick = {
                onConfirm()
                onDismissRequest()
            }) {
                Text(stringResource(R.string.settings_reload_ok))
            }

            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.settings_reload_cancel))
            }
        }
    }
}
