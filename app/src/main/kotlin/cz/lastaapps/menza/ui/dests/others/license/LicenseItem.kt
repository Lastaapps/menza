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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseItem(library: Library, modifier: Modifier = Modifier) {
    ElevatedCard(
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LicenseName(library.name, Modifier.weight(1f))
                LicenseVersion(library.artifactVersion)
            }
            library.developers.forEach { developer ->
                LicenseDeveloper(developer.name)
            }
            library.licenses.map { it.name }.forEach {
                LicenseLicense(it)
            }
        }
    }
}

@Composable
private fun LicenseName(name: String, modifier: Modifier = Modifier) {
    Text(name, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}

@Composable
private fun LicenseDeveloper(developer: String?, modifier: Modifier = Modifier) {
    if (developer != null)
        Text(developer, style = MaterialTheme.typography.bodyLarge, modifier = modifier)
}

@Composable
private fun LicenseLicense(license: String, modifier: Modifier = Modifier) {
    Text(license, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}

@Composable
private fun LicenseVersion(version: String?, modifier: Modifier = Modifier) {
    if (version != null)
        Text(version, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}


