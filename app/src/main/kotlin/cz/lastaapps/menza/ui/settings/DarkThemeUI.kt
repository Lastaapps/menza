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

package cz.lastaapps.menza.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.ui.settings.store.DarkMode
import cz.lastaapps.menza.ui.settings.store.darkMode
import kotlin.math.min


@Composable
fun DarkThemeSettings(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val selected by viewModel.sett.darkMode.collectAsState()

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
    ) {

        Text(text = "App theme:")

        val items = listOf(
            DarkThemeItem("Light", Icons.Default.WbSunny, DarkMode.Light),
            DarkThemeItem("System", Icons.Default.BrightnessMedium, DarkMode.System),
            DarkThemeItem("Dark", Icons.Default.Brightness3, DarkMode.Dark),
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

            val size = measurable.size

            val placeableWidth =
                measurable.map { it.minIntrinsicWidth(constrains.maxWidth / size) }.maxOf { it }
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
                val remainingWidth = width - (placeableWidth * size)
                val spacing = remainingWidth / (size + 1)

                placeables.forEachIndexed { index, placeable ->
                    val offset = index * (spacing + placeableWidth) + spacing
                    val center = (placeableWidth - placeable.width) / 2
                    placeable.placeRelative(offset + center, 0)
                }
            }
        }

    }
}

@Composable
private fun ThemeItem(
    item: DarkThemeItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit,
) {
    val color = if (isSelected)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.tertiaryContainer

    Surface(
        color = color,
        onClick = { onItemSelected() },
        modifier = modifier,
        tonalElevation = 16.dp,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.padding(4.dp),
        ) {
            Surface(
                color = color,
                tonalElevation = 32.dp,
            ) {
                Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Icon(item.icon, contentDescription = item.title)
                }
            }
            Text(item.title)
        }
    }
}

private data class DarkThemeItem(
    val title: String, val icon: ImageVector, val mode: DarkMode,
)
