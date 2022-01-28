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

package cz.lastaapps.menza.ui.layout.menza

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
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
    val scrollState = rememberScrollState()

    val isReady by menzaListViewModel.isReady.collectAsState()
    if (isReady) {
        val menzaList by menzaListViewModel.data.collectAsState()
        Column(
            modifier
                .animateContentSize()
                .width(IntrinsicSize.Max)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            menzaList.forEach { item ->
                MenzaItem(
                    menza = item, selected = item.menzaId == selectedMenza,
                    expanded = expanded,
                    onClick = onMenzaSelected,
                    modifier = Modifier.fillMaxWidth()
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
        modifier = modifier.height(48.dp),
        color = color,
        onClick = { onClick(menza.menzaId) },
        shape = GenericShape { size, direction ->
            if (LayoutDirection.Ltr == direction) {
                addRect(Rect(0f, 0f, size.width - size.height / 2, size.height))
                addOval(Rect(size.width - size.height, 0f, size.width, size.height))
            } else {
                addRect(Rect(size.width, 0f, size.height / 2, size.height))
                addOval(Rect(size.height, 0f, 0f, size.height))
            }
        }
    ) {
        Row(
            modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
        ) {
            MenzaLetter(menza)

            if (expanded) {
                Text(menza.name)
            }
        }
    }
}

@Composable
private fun MenzaLetter(menza: Menza, modifier: Modifier = Modifier) {
    val colors = colorForMenza(menza)
    val brush = Brush.horizontalGradient(colors)
    val size = 32.dp

    Box(
        modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            drawCircle(brush)
        }
        Text(
            text = "" + menza.shorterName[0],
            color = Color(0xffffffff)
        )
    }
}
