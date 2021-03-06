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

package cz.lastaapps.menza.ui.dests.settings.modules

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.DarkMode
import cz.lastaapps.menza.ui.dests.settings.store.darkMode
import kotlin.math.min


@Composable
fun DarkThemeSettings(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val selected by viewModel.sett.darkMode.collectAsState()

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {

        Text(stringResource(R.string.settings_theme_title))

        val items = listOf(
            DarkThemeItem(R.string.settings_theme_light, Icons.Default.WbSunny, DarkMode.Light),
            DarkThemeItem(
                R.string.settings_theme_system, Icons.Default.BrightnessMedium, DarkMode.System
            ),
            DarkThemeItem(R.string.settings_theme_dark, Icons.Default.Brightness3, DarkMode.Dark),
        )
        Layout(
            content = {
                items.forEach { item ->
                    val isSelected = item.mode == selected
                    ThemeItem(item = item, isSelected = isSelected) {
                        viewModel.setDarkMode(item.mode)
                    }
                }
            },
        ) { measurable, constrains ->

            val itemNumber = measurable.size
            val maxItemWidth = constrains.maxWidth / itemNumber

            val placeableWidth =
                measurable.map { it.minIntrinsicWidth(maxItemWidth) }
                    .maxOf { it }
                    //.takeIf {it <= maxItemWidth} ?: maxItemWidth
                    .let { min(it, maxItemWidth) }
            val placeablesHeight =
                measurable.map { it.minIntrinsicHeight(constrains.maxHeight) }.maxOf { it }

            val smallerConst = Constraints(
                placeableWidth, placeableWidth,
                placeablesHeight, placeablesHeight,
            )

            val placeables = measurable.map {
                it.measure(smallerConst)
            }

            val width = constrains.maxWidth
            val height = min(placeables.first().height, constrains.maxHeight)

            layout(width, height) {
                val remainingWidth = width - (placeableWidth * itemNumber)
                val spacing = remainingWidth / (itemNumber + 1)

                placeables.forEachIndexed { index, placeable ->
                    val offset = index * (spacing + placeableWidth) + spacing
                    val center = (placeableWidth - placeable.width) / 2
                    placeable.placeRelative(offset + center, 0)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeItem(
    item: DarkThemeItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit,
) {
    val color = if (isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.tertiary

    val interaction = remember { MutableInteractionSource() }
    Card(
        onClick = onItemSelected,
        interactionSource = interaction,
        colors = CardDefaults.cardColors(
            containerColor = animateColorAsState(color).value,
        ),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterHorizontally),
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(item.icon, stringResource(item.title))
            }
            Text(stringResource(item.title))
        }
    }
}

private data class DarkThemeItem(
    @StringRes val title: Int, val icon: ImageVector, val mode: DarkMode,
)
