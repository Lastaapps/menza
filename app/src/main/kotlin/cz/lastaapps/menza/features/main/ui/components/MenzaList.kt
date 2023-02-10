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

package cz.lastaapps.menza.features.main.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.components.draggablelazylist.DraggableLazyColumn
import cz.lastaapps.menza.ui.components.draggablelazylist.rememberDraggableLazyListState
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel

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
        val state = rememberDraggableLazyListState(
            lazyListState,
            onMove = { from, to ->
                val list = updatableMenzaList.value
                val tmp = list[from]
                list[from] = list[to]
                list[to] = tmp
            },
            onMoveFinished = {
                menzaViewModel.saveNewOrder(updatableMenzaList.value)
            },
        )

        DraggableLazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            itemsIndexed(menzaList) { index, item ->
//                MenzaItem(
//                    menza = item, selected = item.menzaId == selectedMenza,
//                    onClick = onMenzaSelected,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .makeDraggableItem(state, index),
//                )
            }
        }
    }
}
