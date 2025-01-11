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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DishListMode
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DishListViewModeSwitch(
    currentMode: DishListMode?,
    onModeChange: (mode: DishListMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val buttons =
        remember {
            persistentListOf(
                DishListMode.CAROUSEL to R.string.today_list_mode_carousel,
                DishListMode.GRID to R.string.today_list_mode_grid,
                DishListMode.HORIZONTAL to R.string.today_list_mode_horizontal,
                DishListMode.COMPACT to R.string.today_list_mode_compact,
            )
        }

    Card(
        modifier = modifier.fillMaxWidth(),
    ) {
        val padding = Padding.MidSmall
        Text(
            stringResource(R.string.today_list_mode_title),
            style = MaterialTheme.typography.titleMedium,
            modifier =
                Modifier
                    .padding(top = padding)
                    .padding(horizontal = padding)
                    .align(Alignment.CenterHorizontally),
        )

        Spacer(modifier = Modifier.height(Padding.Smaller))

        FlowRow(
            modifier =
                Modifier
                    .padding(bottom = padding)
                    .padding(horizontal = padding)
                    .align(Alignment.CenterHorizontally),
            maxItemsInEachRow = 2,
            horizontalArrangement =
                Arrangement.spacedBy(
                    Padding.Medium,
                    Alignment.CenterHorizontally,
                ),
        ) {
            buttons.forEach { (mode, textId) ->
                if (currentMode == mode) {
                    FilledTonalButton({ onModeChange(mode) }) {
                        Text(text = stringResource(id = textId))
                    }
                } else {
                    OutlinedButton({ onModeChange(mode) }) {
                        Text(text = stringResource(id = textId))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun DishListViewModeSwitchPreview() =
    PreviewWrapper {
        DishListViewModeSwitch(
            currentMode = DishListMode.GRID,
            onModeChange = { },
            modifier = Modifier,
        )
    }
