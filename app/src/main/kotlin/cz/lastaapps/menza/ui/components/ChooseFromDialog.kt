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

package cz.lastaapps.menza.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import cz.lastaapps.menza.ui.util.WrapClick
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun <T> ChooseFromDialog(
    title: String,
    items: ImmutableList<T>,
    onItemSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    toString: (T) -> String = { it.toString() },
) {
    BaseDialog(
        onDismissRequest = onDismiss,
        scrollState = null,
    ) {
        ChooseFromDialogContent(
            title = title,
            items = items,
            onItemSelect = onItemSelect,
            onDismiss = onDismiss,
            toString = toString,
        )
    }
}

@Composable
private fun <T> ChooseFromDialogContent(
    title: String,
    items: ImmutableList<T>,
    onItemSelect: (T) -> Unit,
    onDismiss: () -> Unit,
    toString: (T) -> String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Padding.Medium),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        LazyColumn(modifier = Modifier.fillMaxWidth(.8f)) {
            items(items) { item ->
                WrapClick(
                    onClick = {
                        onItemSelect(item)
                        onDismiss()
                    },
                ) {
                    Text(
                        text = toString(item),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChooseFromDialogPreview() =
    PreviewWrapper {
        ChooseFromDialogContent(
            title = "Great title",
            items = persistentListOf("Ahoj", "Jak", "Se", "Máš?", "Ale znáš to"),
            onItemSelect = {},
            onDismiss = {},
            toString = { it },
        )
    }
