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

package cz.lastaapps.menza.features.main.ui.screen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
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
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionState
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionViewModel
import cz.lastaapps.menza.ui.components.MenzaLetter
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.collections.immutable.ImmutableList
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun MenzaSelectionScreen(
    onEdit: () -> Unit,
    onMenzaSelected: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenzaSelectionViewModel = koinViewModel(),
) {
    MenzaSelectionListEffects(viewModel)

    val state by viewModel.flowState
    MenzaSelectionListContent(
        state = state,
        onEdit = onEdit,
        onMenzaSelected = {
            viewModel.selectMenza(it)
            onMenzaSelected()
        },
        modifier = modifier,
    )
}

@Composable
private fun MenzaSelectionListEffects(
    viewModel: MenzaSelectionViewModel,
) {
    HandleAppear(viewModel)
}

@Composable
private fun MenzaSelectionListContent(
    state: MenzaSelectionState,
    onEdit: () -> Unit,
    onMenzaSelected: (Menza) -> Unit,
    modifier: Modifier = Modifier,
    lazyState: LazyListState = rememberLazyListState(),
) {
    val animateFrom = if (state.fromTop) {
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
            onMenzaSelected = onMenzaSelected,
            onEdit = onEdit,
            lazyState = lazyState,
            modifier = Modifier
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
    onMenzaSelected: (Menza) -> Unit,
    onEdit: () -> Unit,
    lazyState: LazyListState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = lazyState,
        reverseLayout = !fromTop,
        verticalArrangement = Arrangement.spacedBy(
            MenzaPadding.Medium,
            if (fromTop) Alignment.Top else Alignment.Bottom,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        items(menzaList) { menza ->
            MenzaItem(
                menza = menza,
                selected = selectedMenza == menza,
                onClick = onMenzaSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            FilledTonalButton(
                onClick = onEdit,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
                ) {
                    Icon(Icons.Default.Edit, null)
                    Text(stringResource(R.string.button_edit))
                }
            }
        }

        item {
            Icon(
                painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier
                    .size(128.dp)
                    .scale(2f),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        shape = GenericShape { size, direction ->
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
