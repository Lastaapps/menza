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

package cz.lastaapps.menza.ui.dests.week

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.locals.WindowSizeClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekLayout(
    navController: NavController,
    menzaId: MenzaId?,
    weekViewModel: WeekViewModel,
) {
    Crossfade(targetState = menzaId) { currentMenzaId ->
        when (LocalWindowWidth.current) {
            WindowSizeClass.COMPACT ->
                WeekLayoutCompact(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = weekViewModel,
                )
            WindowSizeClass.MEDIUM ->
                WeekLayoutMedium(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = weekViewModel,
                )
            WindowSizeClass.EXPANDED ->
                WeekLayoutExpanded(
                    navController = navController,
                    menzaId = currentMenzaId,
                    viewModel = weekViewModel,
                )

        }
    }
}

@Composable
private fun WeekLayoutCompact(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: WeekViewModel,
) {
        WeekDishList(
            navController = navController,
            menzaId = menzaId,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
        )
}

@Composable
private fun WeekLayoutMedium(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: WeekViewModel,
) {
        WeekDishList(
            navController = navController,
            menzaId = menzaId,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
        )
}

@Composable
private fun WeekLayoutExpanded(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: WeekViewModel,
) {
        WeekDishList(
            navController = navController,
            menzaId = menzaId,
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize(),
        )
}






