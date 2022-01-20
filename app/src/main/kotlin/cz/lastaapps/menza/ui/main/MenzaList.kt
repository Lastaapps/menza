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

package cz.lastaapps.menza.ui.main

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.theme.colorForMenza

@Composable
fun MenzaList(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaViewModel,
) {
    val isReady by menzaListViewModel.isReady.collectAsState()
    if (isReady) {
        val menzaList by menzaListViewModel.data.collectAsState()
        LazyColumn(
            modifier.animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Bottom),
            horizontalAlignment = Alignment.Start,
        ) {
            items(menzaList) { item ->
                MenzaItem(
                    menza = item, selected = item.menzaId == selectedMenza,
                    expanded = expanded,
                    onClick = onMenzaSelected,
                )
            }
        }
    }
}

@Composable
private fun MenzaItem(
    menza: Menza,
    selected: Boolean,
    onClick: (MenzaId) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val color = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.secondary

    Surface(
        modifier = modifier,
        color = color,
        onClick = { onClick(menza.menzaId) },
    ) {
        Row(
            modifier
                .animateContentSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(Brush.horizontalGradient(colorForMenza(menza))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "" + menza.name[0],
                )
            }

            if (expanded)
                Text(menza.name)
        }
    }
}
