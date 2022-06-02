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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.components.draggablelazylist.DraggableLazyColumn
import cz.lastaapps.menza.ui.components.draggablelazylist.makeDraggableItem
import cz.lastaapps.menza.ui.components.draggablelazylist.rememberDraggableLazyListState
import cz.lastaapps.menza.ui.theme.colorForMenza

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenzaList(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    lazyListState: LazyListState,
    modifier: Modifier = Modifier,
) {
    val isReady by menzaViewModel.isReady.collectAsState()

    if (isReady) {
        val menzaListPermanent by menzaViewModel.data.collectAsState()
        val menzaList = remember(menzaListPermanent) {
            mutableStateListOf<Menza>().also { list -> list.addAll(menzaListPermanent) }
        }
        val updatableMenzaList = rememberUpdatedState(newValue = menzaList)
        val state = rememberDraggableLazyListState(lazyListState, onMove = { from, to ->
            val list = updatableMenzaList.value
            val tmp = list[from]
            list[from] = list[to]
            list[to] = tmp
        }, onMoveFinished = {
            menzaViewModel.saveNewOrder(updatableMenzaList.value)
        })

        DraggableLazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            itemsIndexed(menzaList) { index, item ->
                MenzaItem(
                    menza = item, selected = item.menzaId == selectedMenza,
                    onClick = onMenzaSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .makeDraggableItem(state, index),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenzaItem(
    menza: Menza,
    selected: Boolean,
    onClick: (MenzaId) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationDrawerItem(
        icon = { MenzaLetter(menza) },
        label = { Text(menza.name) },
        selected = selected,
        onClick = { onClick(menza.menzaId) },
        shape = GenericShape { size, direction ->
            if (LayoutDirection.Ltr == direction) {
                addRect(Rect(0f, 0f, size.width - size.height / 2, size.height))
                addOval(Rect(size.width - size.height, 0f, size.width, size.height))
            } else {
                addRect(Rect(size.width, 0f, size.height / 2, size.height))
                addOval(Rect(size.height, 0f, 0f, size.height))
            }
        },
        modifier = modifier,
    )
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
