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

package cz.lastaapps.menza.ui.dests.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.dests.others.AboutUi
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
import cz.lastaapps.menza.ui.root.AppLayoutMedium

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
    val aboutShown by settingsViewModel.aboutShown.collectAsState()
    val onAboutClicked = { settingsViewModel.showAbout(!aboutShown) }

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT ->
            SettingsLayoutCompact(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                viewModel = settingsViewModel,
                aboutShown = aboutShown,
                onAboutClicked = onAboutClicked,
            )
        WindowSizeClass.MEDIUM ->
            SettingsLayoutMedium(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                viewModel = settingsViewModel,
                aboutShown = aboutShown,
                onAboutClicked = onAboutClicked,
            )
        WindowSizeClass.EXPANDED ->
            SettingsLayoutExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                viewModel = settingsViewModel,
            )
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
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
    aboutShown: Boolean,
    onAboutClicked: () -> Unit,
) {
    AppLayoutCompact(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        settingsViewModel = settingsViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = aboutShown,
        onBackArrowClick = { onAboutClicked() }) {
        BackHandler(aboutShown) {
            onAboutClicked()
        }
        SettingsUI(
            navController = navController,
            viewModel = viewModel,
            menzaViewModel = menzaViewModel,
            settingsViewModel = settingsViewModel,
            modifier = Modifier.fillMaxSize(),
            enableAbout = true,
            aboutShown = aboutShown,
            onAboutClicked = onAboutClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLayoutMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
    aboutShown: Boolean,
    onAboutClicked: () -> Unit,
) {
    AppLayoutMedium(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        settingsViewModel = settingsViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = aboutShown,
        onBackArrowClick = onAboutClicked,
    ) {
        BackHandler(aboutShown) {
            onAboutClicked()
        }
        SettingsUI(
            navController = navController,
            viewModel = viewModel,
            menzaViewModel = menzaViewModel,
            settingsViewModel = settingsViewModel,
            modifier = Modifier.fillMaxSize(),
            enableAbout = true,
            aboutShown = aboutShown,
            onAboutClicked = onAboutClicked,
        )
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
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        settingsViewModel = settingsViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = false,
        panel1 = {
            SettingsUI(
                navController = navController,
                viewModel = viewModel,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
                enableAbout = false,
            )
        },
        panel2 = {
            AboutUi(
                navController = navController,
                scrollState = rememberScrollState(),
                modifier = Modifier.fillMaxSize()
            )
        },
    )
}