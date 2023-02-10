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

package cz.lastaapps.menza.ui.dests.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.menza.features.main.ui.layout.UseSplitLayout
import cz.lastaapps.menza.ui.dests.others.AboutUi
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.BackArrow
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsLayout(
    navController: NavController,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
) {
    val aboutShown by settingsViewModel.aboutShown.collectAsState()
    val onAboutClicked = { settingsViewModel.showAbout(!aboutShown) }

    when (LocalWindowWidth.current) {
        WindowWidthSizeClass.Compact ->
            SettingsLayoutCompact(
                navController = navController,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                viewModel = settingsViewModel,
                aboutShown = aboutShown,
                onAboutClicked = onAboutClicked,
            )

        WindowWidthSizeClass.Medium ->
            SettingsLayoutMedium(
                navController = navController,
                menzaViewModel = menzaViewModel,
                settingsViewModel = settingsViewModel,
                viewModel = settingsViewModel,
                aboutShown = aboutShown,
                onAboutClicked = onAboutClicked,
            )

        WindowWidthSizeClass.Expanded ->
            SettingsLayoutExpanded(
                navController = navController,
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
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
    aboutShown: Boolean,
    onAboutClicked: () -> Unit,
) {
    BackArrow(enabled = aboutShown) {
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

@Composable
fun SettingsLayoutMedium(
    navController: NavController,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
    aboutShown: Boolean,
    onAboutClicked: () -> Unit,
) = SettingsLayoutCompact(
    navController = navController,
    menzaViewModel = menzaViewModel,
    settingsViewModel = settingsViewModel,
    viewModel = viewModel,
    aboutShown = aboutShown,
    onAboutClicked = onAboutClicked,
)

@Composable
fun SettingsLayoutExpanded(
    navController: NavController,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    viewModel: SettingsViewModel,
) {
    UseSplitLayout(
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
        }
    )
}