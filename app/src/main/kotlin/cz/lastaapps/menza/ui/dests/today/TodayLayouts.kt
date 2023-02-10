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

package cz.lastaapps.menza.ui.dests.today

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.day.Dish
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.features.main.ui.layout.UseSplitLayout
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.root.BackArrow
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth

@Composable
fun TodayDest(
    navController: NavController,
    menzaId: MenzaId?,
    todayViewModel: TodayViewModel,
    settingsViewModel: SettingsViewModel,
) {
    SideEffect {
        todayViewModel.menzaSpotted(menzaId)
    }

    val selectedDish by todayViewModel.selectedDish.collectAsState()
    val onDishSelected: (Dish?) -> Unit = { todayViewModel.selectDish(it) }

    Crossfade(targetState = menzaId) { currentMenzaId ->
        when (LocalWindowWidth.current) {
            WindowWidthSizeClass.Compact -> {
                TodayDestCompact(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = todayViewModel,
                    selectedDish = selectedDish,
                    onDishSelected = onDishSelected,
                    settingsViewModel = settingsViewModel,
                )
            }

            WindowWidthSizeClass.Medium -> {
                TodayDestMedium(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = todayViewModel,
                    selectedDish = selectedDish,
                    onDishSelected = onDishSelected,
                    settingsViewModel = settingsViewModel,
                )
            }

            WindowWidthSizeClass.Expanded -> {
                TodayDestExpanded(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = todayViewModel,
                    selectedDish = selectedDish,
                    onDishSelected = onDishSelected,
                    settingsViewModel = settingsViewModel,
                )
            }
        }
    }
}

@Composable
fun TodayDestCompact(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    BackArrow(enabled = selectedDish != null) {
        onDishSelected(null)
    }

    val scroll = rememberLazyListState()
    Crossfade(targetState = selectedDish) { currentSelectedDish ->
        if (currentSelectedDish == null) {
            TodayDishList(
                navController = navController,
                menzaId = menzaId, onDishSelected = onDishSelected,
                viewModel = viewModel, settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
                scroll = scroll,
            )
        } else {
            TodayInfo(dish = currentSelectedDish, viewModel, Modifier.fillMaxSize())
        }
    }
}

@Composable
fun TodayDestMedium(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    BackArrow(enabled = selectedDish != null) {
        onDishSelected(null)
    }
    Crossfade(targetState = selectedDish) { currentSelectedDish ->
        if (currentSelectedDish == null) {
            TodayDishList(
                navController = navController,
                menzaId = menzaId,
                onDishSelected = onDishSelected,
                viewModel = viewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            TodayInfo(dish = currentSelectedDish, viewModel, Modifier.fillMaxSize())
        }
    }
}

@Composable
fun TodayDestExpanded(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: TodayViewModel,
    selectedDish: Dish?,
    onDishSelected: (Dish?) -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    UseSplitLayout(panel1 = {
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
            Crossfade(targetState = selectedDish) { currentSelectedDish ->
                if (currentSelectedDish == null) {
                    NoDishSelected(Modifier.fillMaxSize())
                } else {
                    TodayInfo(currentSelectedDish, viewModel, Modifier.fillMaxSize())
                }
            }
        })
}