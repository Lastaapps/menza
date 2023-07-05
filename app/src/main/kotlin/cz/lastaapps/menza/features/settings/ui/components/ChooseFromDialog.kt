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

package cz.lastaapps.menza.features.settings.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.bumble.appyx.core.collections.ImmutableList
import cz.lastaapps.menza.ui.util.WrapClick

@Composable
internal fun <T> ChooseFromDialog(
    title: String,
    items: ImmutableList<T>,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit,
    toString: (T) -> String = { it.toString() },
) {
    Dialog(onDismissRequest = onDismiss) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
        )

        LazyColumn {
            items(items) { item ->
                WrapClick(
                    onClick = { onItemSelected(item) },
                ) {
                    Text(text = toString(item))
                }
            }
        }
    }
}
