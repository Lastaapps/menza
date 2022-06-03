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

package cz.lastaapps.menza.ui.dests.others.whatsnew

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.R
import kotlinx.coroutines.flow.map

@Composable
fun whatsNewPanelState(
    whatsNewViewModel: WhatsNewViewModel,
): State<Boolean> {
    return remember { whatsNewViewModel.shouldShow(BuildConfig.VERSION_CODE) }.collectAsState(
        false
    )
}

@Composable
fun WhatsNewPanel(
    whatsNewViewModel: WhatsNewViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val item by remember(context.resources.configuration) {
        val locales = context.getLocales()
        whatsNewViewModel.getDataForLocales(locales).map { it.firstOrNull() }
    }.collectAsState(null)

    PanelContent(
        item ?: return,
        { whatsNewViewModel.dismissed(BuildConfig.VERSION_CODE) },
        modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PanelContent(info: WhatsNewInfo, onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.panel_whats_new_title),
            style = MaterialTheme.typography.titleLarge
        )
        Text("${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE}")

        ElevatedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text(text = info.message, Modifier.padding(12.dp))
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary,
            ),
        ) {
            Text(stringResource(R.string.panel_whats_new_button))
        }
    }
}
