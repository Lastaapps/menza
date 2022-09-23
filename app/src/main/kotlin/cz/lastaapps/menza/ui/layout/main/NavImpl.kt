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

package cz.lastaapps.menza.ui.layout.main

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import cz.lastaapps.menza.R
import cz.lastaapps.menza.navigation.Dest

data class NavItem(
    @StringRes val label: Int,
    val dest: String,
    val icon: ImageVector,
)

object NavImpl {
    val navItems = listOf(
        NavItem(R.string.nav_today, Dest.R.today, Icons.Filled.DinnerDining),
        NavItem(R.string.nav_week, Dest.R.week, Icons.Filled.MenuBook),
        NavItem(R.string.nav_info, Dest.R.info, Icons.Filled.Info),
        NavItem(R.string.nav_settings, Dest.R.settings, Icons.Filled.Settings),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeIcon(icon: ImageVector, showBadge: Boolean) {
    if (showBadge)
        BadgedBox(badge = { Badge {} }) {
            Icon(icon, null)
        }
    else
        Icon(icon, null)
}