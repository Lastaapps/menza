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

package cz.lastaapps.menza.features.main.ui.screen

import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.menza.features.main.ui.layout.MenzaNavigationBar
import cz.lastaapps.menza.features.main.ui.layout.MenzaRail
import cz.lastaapps.menza.features.main.ui.layout.MenzaScaffold
import cz.lastaapps.menza.features.main.ui.layout.MenzaTopBar
import cz.lastaapps.menza.features.main.ui.layout.NavItem
import cz.lastaapps.menza.features.main.ui.layout.TopBarNavTarget
import cz.lastaapps.menza.features.main.ui.navigation.MainNavTarget

@Composable
internal fun MainScreen(
    currentDest: MainNavTarget?,
    drawerState: DrawerState,
    settingsEverOpened: Boolean,
    hostState: SnackbarHostState,
    selectedMenza: Menza?,
    alternativeNavigation: Boolean,
    isFlip: Boolean,
    onNavItemTopBar: (MainNavTarget) -> Unit,
    onNavItemRoot: (MainNavTarget) -> Unit,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val navItem =
        remember(currentDest) {
            currentDest?.toNavItem()
        }

    MenzaScaffold(
        drawerState = drawerState,
        alternativeNavigation = alternativeNavigation,
        snackbarHost = { SnackbarHost(hostState) },
        topBar = { topBarState ->
            MenzaTopBar(
                state = topBarState,
                drawerState = drawerState,
                menza = selectedMenza,
                onAction = { onNavItemTopBar(it.toMainNavType()) },
            )
        },
        bottomBar = {
            MenzaNavigationBar(
                selectedItem = navItem,
                onNavItem = { onNavItemRoot(it.toMainNavType()) },
                settingsEverOpened = settingsEverOpened,
            )
        },
        rail = {
            MenzaRail(
                selectedItem = navItem,
                onNavItem = { onNavItemRoot(it.toMainNavType()) },
                settingsEverOpened = settingsEverOpened,
            )
        },
        drawerContent = drawerContent,
        content = content,
        isFlip = isFlip,
        modifier = modifier,
    )
}

private fun MainNavTarget.toNavItem(): NavItem? =
    when (this) {
        MainNavTarget.Today -> NavItem.Today
        MainNavTarget.Week -> NavItem.Week
        MainNavTarget.Info -> NavItem.Info
        MainNavTarget.Settings -> NavItem.Settings
        else -> null
    }

private fun NavItem.toMainNavType(): MainNavTarget =
    when (this) {
        NavItem.Today -> MainNavTarget.Today
        NavItem.Week -> MainNavTarget.Week
        NavItem.Info -> MainNavTarget.Info
        NavItem.Settings -> MainNavTarget.Settings
    }

private fun TopBarNavTarget.toMainNavType(): MainNavTarget =
    when (this) {
        TopBarNavTarget.PrivacyPolicy -> MainNavTarget.PrivacyPolicy
        TopBarNavTarget.LicenseNotices -> MainNavTarget.LicenseNotices
        TopBarNavTarget.Osturak -> MainNavTarget.Osturak
    }
