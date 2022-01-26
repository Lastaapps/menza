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

package cz.lastaapps.menza.ui.dests.today

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
import cz.lastaapps.menza.ui.root.AppLayoutMedium
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDest(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    todayViewModel: TodayViewModel,
    settingsViewModel: SettingsViewModel,
) {
    remember(menzaId) { todayViewModel.menzaSpotted(menzaId); null }

    val selectedDish by todayViewModel.selectedDish.collectAsState()
    val onDishSelected: (Dish?) -> Unit = { todayViewModel.selectDish(it) }

    @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            TodayDestCompact(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
                settingsViewModel = settingsViewModel,
            )
        }
        WindowSizeClass.MEDIUM -> {
            TodayDestMedium(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
                settingsViewModel = settingsViewModel,
            )
        }
        WindowSizeClass.EXPANDED -> {
            TodayDestExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
                settingsViewModel = settingsViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestCompact(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    val scope = rememberCoroutineScope()

    AppLayoutCompact(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        enableIcon = true,
        showHamburgerMenu = selectedDish == null,
        onMenuButtonClicked = {
            if (selectedDish == null)
                scope.launch { drawerState.open() }
            else
                onDishSelected(null)
        },
    ) {
        BackHandler(enabled = selectedDish != null) {
            onDishSelected(null)
        }
        if (selectedDish == null) {
            TodayDishList(
                navController = navController,
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            TodayInfo(dish = selectedDish, viewModel, Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    AppLayoutMedium(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        showBackButton = selectedDish != null,
        onBackButtonPressed = {
            onDishSelected(null)
        },
    ) {
        BackHandler(enabled = selectedDish != null) {
            onDishSelected(null)
        }
        if (selectedDish == null) {
            TodayDishList(
                navController = navController,
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            TodayInfo(dish = selectedDish, viewModel, Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        showBackButton = false,
        panel1 = {
            TodayDishList(
                navController = navController,
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        },
        panel2 = {
            if (selectedDish == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Not dish selected")
                }
            } else {
                TodayInfo(dish = selectedDish, viewModel, Modifier.fillMaxSize())
            }
        }
    )
}