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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.osslicenseaccess.ArtifactLicense

@Composable
fun LicenseText(
    selectedArtifact: ArtifactLicense?,
    licenseViewModel: LicenseViewModel,
    modifier: Modifier = Modifier,
) {
    if (selectedArtifact == null) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("No artifact selected")
        }
    } else {
        val text = remember(selectedArtifact) {
            licenseViewModel.getTextForArtifact(selectedArtifact)
        }
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    selectedArtifact.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp),
                )
            }
            Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primaryContainer) {
                LinkifyText(
                    text = text,
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp)
                )
            }
        }
    }
}