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
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.MenzaPadding

@Composable
internal fun CompactViewSwitch(
    isCompact: Boolean,
    onCompactChange: (isCompact: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            MenzaPadding.Medium, Alignment.CenterHorizontally,
        ),
    ) {
        CompactButton(
            onClick = { onCompactChange(true) },
            isEnabled = isCompact,
        ) { Text(stringResource(R.string.today_list_compact_compact)) }

        CompactButton(
            onClick = { onCompactChange(false) },
            isEnabled = !isCompact,
        ) { Text(stringResource(R.string.today_list_compact_grid)) }
    }
}

// TODO replace with segment button when available
@Composable
private fun CompactButton(
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Crossfade(
        targetState = isEnabled,
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
