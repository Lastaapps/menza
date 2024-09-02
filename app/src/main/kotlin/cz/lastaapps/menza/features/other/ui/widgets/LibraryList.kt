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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import co.touchlab.kermit.Logger
import com.mikepenz.aboutlibraries.entity.Library
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LibraryList(
    libraries: ImmutableList<Library>,
    onLibrary: (Library?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.Medium),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            items(libraries, key = { it.artifactId }) { library ->
                LicenseItem(library, Modifier.clickable { onLibrary(library) })
            }
            item {
                Spacer(modifier = Modifier.height(Padding.More.ScrollBottomSpace))
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
            Logger.withTag("AppLicenseButton").i { "Opening app license" }
            url.openUri("https://github.com/Lastaapps/menza/blob/main/LICENSE")
        },
        modifier = modifier,
    ) {
        Text(
            text = stringResource(R.string.license_this_app),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}
