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

package cz.lastaapps.menza.features.main.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.common.Menza
import cz.lastaapps.menza.features.main.ui.layout.MenzaNavigationBar
import cz.lastaapps.menza.features.main.ui.layout.MenzaRail
import cz.lastaapps.menza.features.main.ui.layout.MenzaScaffold
import cz.lastaapps.menza.features.main.ui.layout.MenzaTopBar
import cz.lastaapps.menza.features.main.ui.layout.NavItem
import cz.lastaapps.menza.features.main.ui.layout.TopBarNavTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    currentDest: MainNavType,
    drawerState: DrawerState,
    settingsEverOpened: Boolean,
    hostState: SnackbarHostState,
    selectedMenza: Menza?,
    onNavItem: (MainNavType) -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val navItem = remember(currentDest) {
        currentDest.toNavItem()
    }

    MenzaScaffold(
        drawerState = drawerState,
        snackbarHost = { SnackbarHost(hostState) },
        topBar = { topBarState ->
            MenzaTopBar(
                state = topBarState,
                drawerState = drawerState,
                menza = selectedMenza,
                onAction = { onNavItem(it.toMainNavType()) },
            )
        },
        bottomBar = {
            MenzaNavigationBar(
                selectedItem = navItem,
                onNavItem = { onNavItem(it.toMainNavType()) },
                settingsEverOpened = settingsEverOpened,
            )
        },
        rail = {
            MenzaRail(
                selectedItem = navItem,
                onNavItem = { onNavItem(it.toMainNavType()) },
                settingsEverOpened = settingsEverOpened,
            )
        },
        drawerContent = drawerContent,
        content = content,
        modifier = modifier,
    )
}


private fun MainNavType.toNavItem(): NavItem? =
    when (this) {
        MainNavType.Today -> NavItem.Today
        MainNavType.Week -> NavItem.Week
        MainNavType.Info -> NavItem.Info
        MainNavType.Settings -> NavItem.Settings
        else -> null
    }

private fun NavItem.toMainNavType(): MainNavType =
    when (this) {
        NavItem.Today -> MainNavType.Today
        NavItem.Week -> MainNavType.Week
        NavItem.Info -> MainNavType.Info
        NavItem.Settings -> MainNavType.Settings
    }

private fun TopBarNavTarget.toMainNavType(): MainNavType =
    when (this) {
        TopBarNavTarget.PrivacyPolicy -> MainNavType.PrivacyPolicy
        TopBarNavTarget.LicenseNotices -> MainNavType.LicenseNotices
        TopBarNavTarget.Osturak -> MainNavType.Osturak
    }
