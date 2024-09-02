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

package cz.lastaapps.menza.features.other.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.entity.Library
import cz.lastaapps.menza.ui.theme.Padding

@Composable
fun LicenseItem(
    library: Library,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Padding.Small),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Padding.Small)) {
                LicenseName(
                    name = library.name,
                    modifier = Modifier.weight(1f),
                )
                LicenseVersion(
                    version = library.artifactVersion,
                )
            }
            library.developers.forEach { developer ->
                LicenseDeveloper(
                    developer = developer.name,
                )
            }
            library.licenses.map { it.name }.forEach { license ->
                LicenseLicense(
                    license = license,
                )
            }
        }
    }
}

@Composable
private fun LicenseName(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(name, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}

@Composable
private fun LicenseDeveloper(
    developer: String?,
    modifier: Modifier = Modifier,
) {
    if (developer != null) {
        Text(developer, style = MaterialTheme.typography.bodyLarge, modifier = modifier)
    }
}

@Composable
private fun LicenseLicense(
    license: String,
    modifier: Modifier = Modifier,
) {
    Text(license, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
}

@Composable
private fun LicenseVersion(
    version: String?,
    modifier: Modifier = Modifier,
) {
    if (version != null) {
        Text(version, style = MaterialTheme.typography.bodyMedium, modifier = modifier)
    }
}
