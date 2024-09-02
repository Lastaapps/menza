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

package cz.lastaapps.menza.features.settings.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import cz.lastaapps.menza.ui.util.WrapClick

@Composable
internal fun SettingsItem(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) = WrapClick(
    onClick = onClick,
    modifier = modifier,
    padding = SettingsTokens.itemPadding,
) {
    SettingsLabels(
        title = title,
        subtitle = subtitle,
    )
}

@Composable
internal fun SettingsSwitch(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    modifier: Modifier = Modifier,
    onCheck: (Boolean) -> Unit,
) = WrapClick(
    onClick = { onCheck(!isChecked) },
    modifier = modifier,
    padding = SettingsTokens.itemPadding,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Padding.Medium),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SettingsLabels(
            title = title,
            subtitle = subtitle,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheck,
        )
    }
}

// -------------------------------------------------------------------------------------------------
// --- Helpers -------------------------------------------------------------------------------------
// -------------------------------------------------------------------------------------------------
@Composable
private fun SettingsLabels(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        SettingsTitle(text = title)
        SettingsSubTitle(text = subtitle)
    }
}

@Preview
@Composable
private fun SettingsItemPreview() =
    PreviewWrapper {
        SettingsItem(
            title = "Title title",
            subtitle = "This is a description, idk what more to add...",
            onClick = {},
        )
        SettingsItem(
            title = "Title title",
            subtitle = "This is a description, idk what more to add let's make it even longer so we can wrap.",
            onClick = {},
        )
        SettingsSwitch(
            title = "Title title",
            subtitle = "Turn me on, baby",
            isChecked = true,
            onCheck = {},
        )
    }
