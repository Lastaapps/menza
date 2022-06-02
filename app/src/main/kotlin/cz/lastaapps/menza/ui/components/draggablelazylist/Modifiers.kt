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

package cz.lastaapps.menza.ui.components.draggablelazylist

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.makeDraggableList(
    dragDropListState: DraggableLazyListState,
    scope: CoroutineScope,
) = pointerInput(Unit) {
    detectDragGesturesAfterLongPress(
        onDrag = { change, offset ->
            change.consume()
            dragDropListState.onDrag(offset)

            if (dragDropListState.overscrollJob?.isActive == true)
                return@detectDragGesturesAfterLongPress

            dragDropListState.checkForOverScroll()
                .takeIf { it != 0f }
                ?.let {
                    val job = scope.launch {
                        dragDropListState.lazyListState.scrollBy(it)
                    }
                    dragDropListState.overscrollJob = job
                }
                ?: run { dragDropListState.overscrollJob?.cancel() }
        },
        onDragStart = { offset -> dragDropListState.onDragStart(offset) },
        onDragEnd = { dragDropListState.onDragEnd() },
        onDragCancel = { dragDropListState.onDragInterrupted() }
    )
}

fun Modifier.makeDraggableItem(dragDropListState: DraggableLazyListState, index: Int): Modifier {
    val offsetOrNull =
        dragDropListState.elementDisplacement.takeIf {
            index == dragDropListState.currentIndexOfDraggedItem
        }

    return graphicsLayer {
        translationY = offsetOrNull ?: 0f
    }.zIndex(1f.takeIf { offsetOrNull != null } ?: 0f)
}
