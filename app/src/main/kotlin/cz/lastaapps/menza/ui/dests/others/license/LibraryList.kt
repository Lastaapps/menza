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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.aboutlibraries.entity.Library
import cz.lastaapps.menza.R
import org.lighthousegames.logging.logging

@Composable
fun LibraryList(
    libraries: List<Library>,
    onLibrarySelected: (Library?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(libraries) { library ->
                LicenseItem(library, Modifier.clickable { onLibrarySelected(library) })
            }
            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }

        AppLicenseButton(Modifier.fillMaxWidth())
    }
}

@Composable
private fun AppLicenseButton(modifier: Modifier = Modifier) {
    val url = LocalUriHandler.current
    OutlinedButton(
        onClick = {
            logging("AppLicenseButton").i { "Opening app license" }
            url.openUri("https://github.com/Lastaapps/cvutbus/LICENSE")
        },
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.license_this_app),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

