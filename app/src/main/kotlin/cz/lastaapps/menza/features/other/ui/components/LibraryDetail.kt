/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.other.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.entity.License
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding


@Composable
fun NoLibrarySelected(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(stringResource(R.string.license_none_selected), textAlign = TextAlign.Center)
    }
}

@Composable
fun LibraryDetail(library: Library, modifier: Modifier = Modifier) {
    SelectionContainer(modifier.verticalScroll(rememberScrollState())) {
        Column(Modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {

            val titleStyle = MaterialTheme.typography.titleMedium
            val bodyStyle = MaterialTheme.typography.bodyMedium

            Text(library.name, style = MaterialTheme.typography.headlineSmall)
            Text(library.artifactId, style = titleStyle)

            if (library.openSource) {
                Text(stringResource(R.string.license_detail_opensource_yes), style = bodyStyle)
            } else {
                Text(stringResource(R.string.license_detail_opensource_no), style = bodyStyle)
            }

            if (library.developers.isNotEmpty()) {
                Column {
                    Text(stringResource(R.string.license_detail_developer), style = titleStyle)
                    library.developers.forEach { developer ->
                        developer.name?.let { Text(it, style = bodyStyle) }
                    }
                }
            }

            library.website?.let { website ->
                Column {
                    Text(stringResource(R.string.license_detail_link), style = titleStyle)
                    Uri(website, style = bodyStyle)
                }
            }

            library.organization?.let { organization ->
                Column {
                    Text(stringResource(R.string.license_detail_organization), style = titleStyle)
                    Text(organization.name, style = bodyStyle)
                }
            }

            library.description?.let { description ->
                Column {
                    Text(stringResource(R.string.license_detail_description), style = titleStyle)
                    Text(description, style = bodyStyle)
                }
            }

            if (library.licenses.isEmpty()) {
                Text(stringResource(R.string.license_detail_no_license))
            } else {
                library.licenses.forEach { license ->
                    LicenseDetail(license, titleStyle = titleStyle, bodyStyle = bodyStyle)
                }
            }
        }
    }
}

@Composable
private fun LicenseDetail(
    license: License,
    modifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    bodyStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Padding.MidSmall),
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Text(license.name, style = titleStyle)

            license.year?.takeIf { it.isNotBlank() }?.let { Text(it, style = bodyStyle) }
            license.url?.takeIf { it.isNotBlank() }?.let { Uri(it, style = bodyStyle) }

            license.licenseContent?.takeIf { it.isNotBlank() }?.let { Text(it, style = bodyStyle) }
        }
    }
}

@Composable
private fun Uri(
    link: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    val handler = LocalUriHandler.current
    Text(
        link, textDecoration = TextDecoration.Underline,
        style = style,
        modifier = modifier.clickable {
            Logger.withTag("UriComposable").i { "Opening $link" }
            handler.openUri(link)
        },
    )
}