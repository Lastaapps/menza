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

package cz.lastaapps.menza.features.other.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.common.Communication
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.other.ui.vm.PolicyViewModel
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper

@Composable
@Suppress("ktlint:compose:parameter-naming")
internal fun PrivacyDialogDest(
    onNotNeeded: () -> Unit,
    viewModel: PolicyViewModel,
    isRequired: Boolean,
) {
    val state by viewModel.shouldShow.collectAsState()
    val onNotNeededLambda by rememberUpdatedState(onNotNeeded)

    when (state?.or(!isRequired)) {
        true ->
            PrivacyDialog(
                onDismissRequest = {
                    if (!isRequired) {
                        onNotNeededLambda()
                    }
                },
                showAccept = isRequired,
                onAccept = viewModel::onApprove,
            )

        false ->
            LaunchedEffect(Unit) { onNotNeededLambda() }

        null -> {}
    }
}

@Composable
internal fun PrivacyDialog(
    onDismissRequest: () -> Unit,
    showAccept: Boolean,
    onAccept: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        PrivacyDialogContent(showAccept = showAccept, onAccept = onAccept)
    }
}

@Composable
internal fun PrivacyDialogContent(
    showAccept: Boolean,
    onAccept: () -> Unit,
) {
    Surface(shape = MaterialTheme.shapes.extraLarge) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.privacy_title),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.privacy_text),
                textAlign = TextAlign.Center,
            )

            SUZWarning()

            val context = LocalContext.current
            OutlinedButton(onClick = { Communication.openProjectsGithub(context, "Menza") }) {
                Text(stringResource(R.string.privacy_view_source))
            }
            if (showAccept) {
                Button(onClick = onAccept) {
                    Text(stringResource(R.string.privacy_accept))
                }
            }
        }
    }
}

@Composable
private fun SUZWarning(modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Text(
            stringResource(R.string.privacy_warning),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(Padding.Small),
        )
    }
}

@Preview
@Composable
private fun PrivacyDialogContentPreview() =
    PreviewWrapper {
        PrivacyDialogContent(showAccept = true) { }
    }
