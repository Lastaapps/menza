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

package cz.lastaapps.menza.features.info.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cz.lastaapps.api.core.domain.model.Address
import cz.lastaapps.api.core.domain.model.LatLong
import cz.lastaapps.core.domain.error.CommonError
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList

@JvmName("AddressListImpl")
@Composable
fun AddressList(
    locations: ImmutableList<Address>,
    onError: (DomainError) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    AddressList(
        locations = locations,
        openAddress = { location ->
            location.gps?.let {
                openMap(context, it, onError = {
                    onError(CommonError.AppNotFound.Map)
                })
            }
        },
        modifier = modifier,
    )
}

@Composable
private fun AddressList(
    locations: ImmutableList<Address>,
    openAddress: (Address) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (locations.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.info_location_title),
                style = MaterialTheme.typography.titleLarge
            )
            locations.forEach {
                Address(
                    location = it,
                    openAddress = openAddress,
                )
            }
        }
    }
}

@Composable
private fun Address(
    location: Address,
    openAddress: (Address) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.Small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SelectionContainer {
            Text(
                text = location.location.name.replace(", ", "\n"),
                textAlign = TextAlign.Center,
            )
        }

        location.gps?.let {
            Button(
                onClick = { openAddress(location) },
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.Map, contentDescription = null)
                    Text(stringResource(R.string.info_location_open_app))
                }
            }
        }
    }
}

private fun openMap(context: Context, location: LatLong, onError: () -> Unit) {
    val intent = with(location) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$lat,$long?g=$lat,$long&z=19")
        )
    }
    try {
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(R.string.info_location_choose_app)
            )
        )
    } catch (e: Exception) {
        onError()
        e.printStackTrace()
    }
}

