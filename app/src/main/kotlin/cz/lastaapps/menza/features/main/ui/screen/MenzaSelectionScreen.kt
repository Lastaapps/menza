/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.main.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionState
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionViewModel
import cz.lastaapps.menza.ui.components.MenzaLetter
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun MenzaSelectionScreen(
    onEdit: () -> Unit,
    onMenzaSelect: () -> Unit,
    viewModel: MenzaSelectionViewModel,
    accountBalance: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    MenzaSelectionListEffects(viewModel)

    val state by viewModel.flowState
    MenzaSelectionListContent(
        state = state,
        onEdit = onEdit,
        onMenzaSelect = {
            viewModel.selectMenza(it)
            onMenzaSelect()
        },
        accountBalance = accountBalance,
        modifier = modifier,
    )
}

@Composable
private fun MenzaSelectionListEffects(viewModel: MenzaSelectionViewModel) {
}

@Composable
private fun MenzaSelectionListContent(
    state: MenzaSelectionState,
    onEdit: () -> Unit,
    onMenzaSelect: (Menza) -> Unit,
    accountBalance: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    lazyState: LazyListState = rememberLazyListState(),
) {
    val animateFrom =
        if (state.fromTop) {
            Alignment.TopCenter
        } else {
            Alignment.BottomCenter
        }
    Box(
        modifier = modifier,
        contentAlignment = animateFrom,
    ) {
        MenzaList(
            fromTop = state.fromTop,
            selectedMenza = state.selectedMenza,
            menzaList = state.menzaList,
            onMenzaSelect = onMenzaSelect,
            onEdit = onEdit,
            lazyState = lazyState,
            accountBalance = accountBalance,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
        )
    }
}

@Composable
private fun MenzaList(
    fromTop: Boolean,
    selectedMenza: Menza?,
    menzaList: ImmutableList<Menza>,
    onMenzaSelect: (Menza) -> Unit,
    onEdit: () -> Unit,
    lazyState: LazyListState,
    accountBalance: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyState,
        reverseLayout = !fromTop,
        verticalArrangement =
            Arrangement.spacedBy(
                Padding.Medium,
                if (fromTop) Alignment.Top else Alignment.Bottom,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        item("header") {
            Text(
                text = stringResource(R.string.app_name_long),
                style = MaterialTheme.typography.headlineMedium,
                modifier =
                    Modifier
                        .padding(vertical = Padding.Smaller)
                        .padding(start = Padding.Medium),
            )
        }

        item("balance") {
            accountBalance(Modifier.animateItem())
        }

        items(menzaList, key = { it.type.toString() }) { menza ->
            MenzaItem(
                menza = menza,
                selected = selectedMenza?.type == menza.type,
                onClick = onMenzaSelect,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item("edit") {
            FilledTonalButton(
                onClick = onEdit,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                ) {
                    Icon(Icons.Default.Edit, null)
                    Text(stringResource(R.string.button_edit))
                }
            }
        }

        item("some_icon") {
            Icon(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(128.dp)
                        .scale(2f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item("spacer") {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
internal fun MenzaItem(
    menza: Menza,
    selected: Boolean,
    onClick: (Menza) -> Unit,
    modifier: Modifier = Modifier,
) {
    val openedAlpha by animateFloatAsState(if (menza.isOpened) 1f else .5f)

    NavigationDrawerItem(
        icon = { MenzaLetter(menza) },
        label = { Text(menza.name) },
        selected = selected,
        onClick = { onClick(menza) },
        shape =
            GenericShape { size, direction ->
                if (LayoutDirection.Ltr == direction) {
                    addRect(Rect(0f, 0f, size.width - size.height / 2, size.height))
                    addOval(Rect(size.width - size.height, 0f, size.width, size.height))
                } else {
                    addRect(Rect(size.width, 0f, size.height / 2, size.height))
                    addOval(Rect(size.height, 0f, 0f, size.height))
                }
            },
        modifier = modifier.alpha(openedAlpha),
    )
}
