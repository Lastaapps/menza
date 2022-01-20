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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.main.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLayout(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
) {
    @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            SettingsLayoutCompact(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = settingsViewModel,
            )
        }
        in listOf(WindowSizeClass.MEDIUM, WindowSizeClass.EXPANDED) -> {
            SettingsLayoutExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = settingsViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLayoutCompact(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: SettingsViewModel,
) {
    val scope = rememberCoroutineScope()
    AppLayoutCompact(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        enableIcon = true,
        showHamburgerMenu = true,
        onMenuButtonClicked = {
            scope.launch { drawerState.open() }
        }
    ) {
        SettingsUI(viewModel = viewModel, modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLayoutExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: SettingsViewModel,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        panel1 = { SettingsUI(viewModel = viewModel, modifier = Modifier.fillMaxSize()) },
        panel2 = {},
    )
}