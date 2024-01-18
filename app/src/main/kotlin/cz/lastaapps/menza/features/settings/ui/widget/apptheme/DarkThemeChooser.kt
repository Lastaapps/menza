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

package cz.lastaapps.menza.features.settings.ui.widget.apptheme

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness3
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import kotlin.math.min

@Composable
internal fun DarkThemeChooser(
    selected: DarkMode,
    onSelect: (DarkMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        DarkThemeItem(R.string.settings_theme_dark_light, Icons.Default.WbSunny, DarkMode.Light),
        DarkThemeItem(
            R.string.settings_theme_dark_system, Icons.Default.BrightnessMedium, DarkMode.System,
        ),
        DarkThemeItem(R.string.settings_theme_dark_dark, Icons.Default.Brightness3, DarkMode.Dark),
    )
    Layout(
        modifier = modifier,
        content = {
            items.forEach { item ->
                val isSelected = item.mode == selected
                DarkThemeItem(item = item, isSelected = isSelected) {
                    onSelect(item.mode)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DarkThemeItem(
    item: DarkThemeItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onItemSelected: () -> Unit,
) {
    val color = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.tertiary
    }

    val interaction = remember { MutableInteractionSource() }
    val scale by animateFloatAsState(
        if (isSelected) 1f else DarkThemeChooser.unselectedScale,
        label = "scale",
    )

    val colorContainer by animateColorAsState(color, label = "container_color")
    val colorContent by animateColorAsState(contentColorFor(color), label = "content_color")

    Surface(
        onClick = onItemSelected,
        interactionSource = interaction,
        shape = MaterialTheme.shapes.medium,
        color = colorContainer,
        modifier = modifier.scale(scale),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.padding(8.dp),
        ) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    item.icon,
                    stringResource(item.title),
                    tint = colorContent,
                )
            }
            Text(
                stringResource(item.title),
                color = colorContent,
            )
        }
    }
}

private data class DarkThemeItem(
    @StringRes val title: Int, val icon: ImageVector, val mode: DarkMode,
)

private object DarkThemeChooser {
    const val unselectedScale = .95f
}
