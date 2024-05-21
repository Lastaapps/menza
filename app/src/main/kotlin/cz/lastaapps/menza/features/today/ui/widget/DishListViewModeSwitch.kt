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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import kotlinx.collections.immutable.persistentListOf

@Composable
internal fun DishListViewModeSwitch(
    currentMode: DishListMode?,
    onModeChange: (mode: DishListMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttons = remember {
        persistentListOf(
            DishListMode.COMPACT to R.string.today_list_mode_compact,
            DishListMode.GRID to R.string.today_list_mode_grid,
            DishListMode.HORIZONTAL to R.string.today_list_mode_horizontal,
        )
    }
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.horizontalScroll(rememberScrollState()),
    ) {
        buttons.forEachIndexed { index, (mode, textId) ->
            SegmentedButton(
                selected = mode == currentMode,
                onClick = { onModeChange(mode) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = buttons.size),
            ) {
                Text(stringResource(id = textId))
            }
        }
    }
}
