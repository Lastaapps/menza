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

package cz.lastaapps.menza.features.main.ui.layout

import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.ui.components.BadgeIcon

@Composable
internal fun MenzaRail(
    selectedItem: NavItem?,
    onNavItem: (NavItem) -> Unit,
    settingsEverOpened: Boolean,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier) {
        NavItem.values().forEach { item ->
            val showBadge = item == NavItem.Settings && !settingsEverOpened

            NavigationRailItem(
                icon = { BadgeIcon(item.icon, showBadge) },
                label = { Text(stringResource(item.label)) },
                selected = selectedItem == item,
                onClick = { onNavItem(item) },
                alwaysShowLabel = false,
            )
        }
    }
}

