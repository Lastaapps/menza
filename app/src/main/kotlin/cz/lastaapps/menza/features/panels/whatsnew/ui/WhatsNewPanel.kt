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

package cz.lastaapps.menza.features.panels.whatsnew.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.other.domain.model.WhatsNewInfo
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel
import cz.lastaapps.menza.ui.util.PreviewWrapper

@Composable
internal fun WhatsNewPanel(
    whatsNewViewModel: WhatsNewViewModel,
    modifier: Modifier = Modifier,
) {
    val state by whatsNewViewModel.flowState

    WhatsNewPanel(
        state,
        { whatsNewViewModel.onDismiss() },
        modifier,
    )
}

@Composable
internal fun WhatsNewPanel(
    state: WhatsNewViewModel.State,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    state.news.firstOrNull()?.let { info ->
        PanelContent(
            info,
            onDismiss,
            modifier,
        )
    }
}

@Composable
private fun PanelContent(
    info: WhatsNewInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.panel_whats_new_title),
            style = MaterialTheme.typography.titleLarge,
        )
        Text("${BuildConfig.VERSION_NAME} - ${BuildConfig.VERSION_CODE}")

        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(),
        ) {
            Text(text = info.message, Modifier.padding(12.dp))
        }

        Button(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
        ) {
            Text(stringResource(R.string.panel_whats_new_button))
        }
    }
}

@Preview
@Composable
private fun WhatsNewPanelPreview() =
    PreviewWrapper {
        PanelContent(
            info =
                WhatsNewInfo(
                    versionCode = 123456789L,
                    message =
                        @Suppress("ktlint:standard:max-line-length")
                        "There is a theory which states that if ever anyone discovers exactly what the Universe is for and why it is here, it will instantly disappear and be replaced by something even more bizarre and inexplicable. There is another theory which states that this has already happened.",
                ),
            onDismiss = {},
        )
    }
