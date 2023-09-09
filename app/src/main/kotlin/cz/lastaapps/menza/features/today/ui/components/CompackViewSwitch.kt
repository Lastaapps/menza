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

package cz.lastaapps.menza.features.today.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun CompactViewSwitch(
    currentMode: DishListMode?,
    onCompactChange: (mode: DishListMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttons = remember {
        persistentListOf(
            DishListMode.COMPACT to R.string.today_list_mode_compact,
            DishListMode.GRID to R.string.today_list_mode_grid,
            DishListMode.HORIZONTAL to R.string.today_list_mode_horizontal,
        )
    }
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            Padding.Medium, Alignment.CenterHorizontally,
        ),
    ) {
        items(buttons) { (mode, resId) ->
            CompactButton(
                onClick = { onCompactChange(mode) },
                isSelected = mode == currentMode,
            ) { Text(stringResource(resId)) }
        }
    }
}

// TODO replace with segment button when available
@Composable
private fun CompactButton(
    onClick: () -> Unit,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Crossfade(
        targetState = isSelected,
        modifier = modifier,
    ) { enabled ->
        if (enabled) {
            ElevatedButton(onClick = onClick) {
                content()
            }
        } else {
            TextButton(onClick = onClick) {
                content()
            }
        }
    }
}
