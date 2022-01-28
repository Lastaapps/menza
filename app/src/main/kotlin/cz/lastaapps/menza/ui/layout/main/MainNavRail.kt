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
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.lastaapps.menza.R
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.navigation.routesEquals

data class NavItem(
    @StringRes val label: Int,
    val dest: String,
    val icon: ImageVector,
)

val navItems = listOf(
    NavItem(R.string.nav_today, Dest.R.today, Icons.Filled.DinnerDining),
    NavItem(R.string.nav_week, Dest.R.week, Icons.Filled.MenuBook),
    NavItem(R.string.nav_info, Dest.R.info, Icons.Filled.Info),
    NavItem(R.string.nav_settings, Dest.R.settings, Icons.Filled.Settings),
)

@Composable
fun MainNavRail(
    navController: NavController,
    modifier: Modifier = Modifier,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route

    NavigationRail(modifier) {
        navItems.forEach { item ->
            val selected = route?.routesEquals(item.dest) ?: false

            NavigationRailItem(
                icon = { Icon(item.icon, null) },
                label = { Text(stringResource(item.label)) },
                selected = selected,
                onClick = {
                    navController.navigate(item.dest) {
                        launchSingleTop = true
                        popUpTo(Dest.R.start) {
                            saveState = true
                            inclusive = false
                        }
                    }
                },
                alwaysShowLabel = false
            )
        }
    }
}

