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

package cz.lastaapps.menza.ui.layout.main

import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.navigation.routesEquals
import cz.lastaapps.menza.settings.data.settingsEverOpened
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel

@Composable
fun MainNavRail(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route
    val settingsEverOpened by settingsViewModel.sett.settingsEverOpened.collectAsState()

    NavigationRail(modifier) {
        NavImpl.navItems.forEach { item ->
            val selected = route?.routesEquals(item.dest) ?: false
            val showBadge = item.dest == Dest.R.settings && !settingsEverOpened

            NavigationRailItem(
                icon = { BadgeIcon(item.icon, showBadge) },
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

