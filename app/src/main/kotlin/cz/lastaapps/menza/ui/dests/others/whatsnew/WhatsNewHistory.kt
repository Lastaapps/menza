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

package cz.lastaapps.menza.ui.dests.others.whatsnew

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.lastaapps.menza.BuildConfig
import cz.lastaapps.menza.R

@Composable
fun WhatsNewDialog(
    whatsNewViewModel: WhatsNewViewModel,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest) {
        val source = remember { MutableInteractionSource() }
        Box(
            Modifier
                .fillMaxSize(.9f)
                .clickable(source, null, onClick = onDismissRequest),
            contentAlignment = Alignment.Center
        ) {
            Surface(shape = MaterialTheme.shapes.large, modifier = Modifier.animateContentSize()) {
                WhatsNewHistory(whatsNewViewModel, Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun WhatsNewHistory(whatsNewViewModel: WhatsNewViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val itemsState = remember(context.resources.configuration) {
        val locales = context.getLocales()
        whatsNewViewModel.getDataForLocales(locales)
    }.collectAsState(initial = null)
    val items = itemsState.value

    if (items != null) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.whats_new_title),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(stringResource(R.string.whats_new_subtitle, BuildConfig.VERSION_CODE))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
            ) {
                items(items) {
                    HistoryItem(it, Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryItem(item: WhatsNewInfo, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            Text(stringResource(R.string.whats_new_item_label, item.versionCode))
            Text(item.message)
        }
    }
}
