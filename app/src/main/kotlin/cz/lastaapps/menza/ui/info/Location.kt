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

package cz.lastaapps.menza.ui.info

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cz.lastaapps.entity.menza.Address
import cz.lastaapps.entity.menza.Coordinates
import cz.lastaapps.menza.ui.LocalSnackbarProvider
import kotlinx.coroutines.launch

@Composable
fun OpenMap(location: Coordinates, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val snackbar = LocalSnackbarProvider.current
    val scope = rememberCoroutineScope()

    IconButton(
        onClick = {
            openMap(context, location, onError = {
                scope.launch {
                    snackbar.showSnackbar("No map app found!")
                }
            })
        },
        modifier = modifier,
    ) {
        Icon(Icons.Default.Map, contentDescription = "View on map")
    }
}

private fun openMap(context: Context, location: Coordinates, onError: () -> Unit) {
    val intent = with(location) {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:$long,$lat?g=$long,$lat&z=19")
        )
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Select an application"));
    } catch (e: Exception) {
        onError()
        e.printStackTrace()
    }
}

@Composable
fun Address(address: Address, modifier: Modifier = Modifier) {
    Text(address.stringForm, modifier = modifier)
}

