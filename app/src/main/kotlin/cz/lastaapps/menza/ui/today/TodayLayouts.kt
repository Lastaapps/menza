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

package cz.lastaapps.menza.ui.today

import androidx.activity.compose.BackHandler
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.navigation.NavController
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.main.MenzaViewModel
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
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    todayViewModel: TodayViewModel,
) {
    remember(menzaId) { todayViewModel.menzaSpotted(menzaId); null }

    val selectedDish by todayViewModel.selectedDish.collectAsState()
    val onDishSelected: (Dish?) -> Unit = { todayViewModel.selectDish(it) }

    @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            TodayDestCompat(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
            )
        }
        WindowSizeClass.MEDIUM -> {
            TodayDestMedium(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
            )
        }
        WindowSizeClass.EXPANDED -> {
            TodayDestExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                viewModel = todayViewModel,
                selectedDish = selectedDish,
                onDishSelected = onDishSelected,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestCompat(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
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
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
            )
        } else {
            Text(text = selectedDish.name)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
) {
    AppLayoutMedium(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
    ) {
        BackHandler(enabled = selectedDish != null) {
            onDishSelected(null)
        }
        if (selectedDish == null) {
            TodayDishList(
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
            )
        } else {
            Text(text = selectedDish.name)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayDestExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        panel1 = {
            TodayDishList(
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
            )
        },
        panel2 = {
            if (selectedDish == null) {
                Text("Nothing selected")
            } else {
                Text(text = selectedDish.name)
            }
        }
    )
}