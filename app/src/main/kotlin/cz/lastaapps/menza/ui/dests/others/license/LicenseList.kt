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

package cz.lastaapps.menza.ui.dests.others.license

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.osslicenseaccess.ArtifactLicense

@Composable
fun LicenseList(
    licenseViewModel: LicenseViewModel,
    onArtifactSelected: (ArtifactLicense?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val data = remember {
        licenseViewModel.getList()
    }
    LicenseList(data, onArtifactSelected, modifier)
}

@Composable
fun LicenseList(
    data: List<ArtifactLicense>,
    onArtifactSelected: (ArtifactLicense?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringResource(R.string.license_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        Box(Modifier.weight(1f)) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(data) {
                    Artifact(
                        artifact = it,
                        onClick = { onArtifactSelected(it) },
                        Modifier.fillMaxWidth()
                    )
                }
            }
        }

        ThisApp(Modifier.fillMaxWidth())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Artifact(
    artifact: ArtifactLicense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    ElevatedCard(
        modifier = modifier.clickable(interaction, null, onClick = onClick),
        interactionSource = interaction,
    ) {
        Box(
            Modifier
                .padding(8.dp)
                .defaultMinSize(minHeight = 32.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text = artifact.name)
        }
    }
}

@Composable
private fun ThisApp(
    modifier: Modifier = Modifier
) {
    val url = LocalUriHandler.current
    OutlinedButton(
        onClick = {
            url.openUri("https://github.com/Lastaapps/Menza/LICENSE")
        },
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.license_menza_button),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}


