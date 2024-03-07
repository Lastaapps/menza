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

package cz.lastaapps.menza.features.settings.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.MenzaOrder
import cz.lastaapps.menza.features.settings.ui.vm.ReorderMenzaState
import cz.lastaapps.menza.features.settings.ui.vm.ReorderMenzaViewModel
import cz.lastaapps.menza.ui.components.CheckboxWithText
import cz.lastaapps.menza.ui.components.MenzaLetter
import cz.lastaapps.menza.ui.components.draggablelazylist.DraggableLazyColumn
import cz.lastaapps.menza.ui.components.draggablelazylist.makeDraggableItem
import cz.lastaapps.menza.ui.components.draggablelazylist.rememberDraggableLazyListState
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun ReorderMenzaScreen(
    onDone: () -> Unit,
    viewModel: ReorderMenzaViewModel,
    modifier: Modifier = Modifier,
) {
    ReorderMenzaEffects(viewModel)

    val state by viewModel.flowState
    ReorderMenzaContent(
        state = state,
        onVisibilityClick = viewModel::toggleVisibility,
        onOrderUpdated = viewModel::saveOrder,
        onReverseOrder = viewModel::reverseOrder,
        onDone = onDone,
        modifier = modifier,
    )
}

@Composable
private fun ReorderMenzaEffects(
    viewModel: ReorderMenzaViewModel,
) {
    HandleAppear(viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ReorderMenzaContent(
    state: ReorderMenzaState,
    onVisibilityClick: (Menza) -> Unit,
    onOrderUpdated: (List<Pair<Menza, MenzaOrder>>) -> Unit,
    onReverseOrder: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val menzaList = remember(state.menzaList) {
        mutableStateListOf<Pair<Menza, MenzaOrder>>()
            .also { list -> list.addAll(state.menzaList) }
    }
    val localList = rememberUpdatedState(menzaList)

    val draggableState = rememberDraggableLazyListState(
        onMove = { from, to ->
            val list = localList.value
            val tmp = list[from]
            list[from] = list[to]
            list[to] = tmp
        },
        onMoveFinished = {
            onOrderUpdated(localList.value)
        },
        reverse = !state.fromTop,
    )

    Column(
        modifier = modifier
            .padding(Padding.MidLarge)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Padding.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.menza_order_title),
            style = MaterialTheme.typography.headlineSmall,
        )

        val animateFrom = if (state.fromTop) {
            Alignment.TopEnd
        } else {
            Alignment.BottomStart
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = animateFrom,
        ) {
            DraggableLazyColumn(
                state = draggableState,
                reverseLayout = !state.fromTop,
                verticalArrangement = Arrangement.spacedBy(Padding.Medium),
                modifier = Modifier.animateContentSize(),
            ) {
                itemsIndexed(
                    items = localList.value,
                    key = { _, it -> it.first.type.id },
                ) { index, (menza, order) ->
                    val itemModifier =
                        if (draggableState.currentIndexOfDraggedItem != index) {
                            Modifier.animateItemPlacement()
                        } else {
                            Modifier
                        }

                    MenzaItem(
                        menza = menza,
                        visible = order.visible,
                        onVisibilityClick = { onVisibilityClick(menza) },
                        modifier = itemModifier
                            .makeDraggableItem(draggableState, index),
                    )
                }
            }
        }

        CheckboxWithText(checked = state.fromTop, onCheckedChange = { onReverseOrder() }) {
            Text(stringResource(R.string.menza_order_button_align_top))
        }

        Button(onClick = onDone) {
            Text(stringResource(R.string.button_done))
        }

        Text(
            text = stringResource(R.string.menza_order_text_total, state.menzaList.size),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun MenzaItem(
    menza: Menza,
    visible: Boolean,
    onVisibilityClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alpha by animateFloatAsState(if (visible) 1f else .5f)

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = CircleShape,
        modifier = modifier.alpha(alpha),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Padding.Small),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = Padding.Medium,
                vertical = Padding.Small,
            ),
        ) {
            MenzaLetter(menza)
            Text(
                text = menza.name,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(onClick = onVisibilityClick) {
                Icon(
                    if (visible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = stringResource(
                        if (visible) R.string.menza_order_button_hide_menza else R.string.menza_order_button_show_menza,
                    ),
                )
            }
            Icon(
                Icons.Default.DragHandle,
                contentDescription = null,
            )
        }
    }
}

@Preview
@Composable
private fun MenzaItemPreview() {
    AppTheme {
        Surface {
            Column(Modifier.padding(Padding.Large)) {
                MenzaItem(
                    menza = Menza(
                        Strahov,
                        "Menza Strahov",
                        isOpened = true,
                        supportsDaily = true,
                        supportsWeekly = true,
                        isExperimental = false,
                        videoLinks = persistentListOf(),
                    ),
                    visible = true, onVisibilityClick = {},
                )
                MenzaItem(
                    menza = Menza(
                        Strahov,
                        "Restaurace Strahov",
                        isOpened = true,
                        supportsDaily = true,
                        supportsWeekly = true,
                        isExperimental = false,
                        videoLinks = persistentListOf(),
                    ),
                    visible = false, onVisibilityClick = {},
                )
            }
        }
    }
}
