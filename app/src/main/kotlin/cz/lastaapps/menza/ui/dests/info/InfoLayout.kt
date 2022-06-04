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

package cz.lastaapps.menza.ui.dests.info

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.root.UseSplitLayout
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.locals.WindowSizeClass

@Composable
fun InfoLayout(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    menzaId: MenzaId?,
    infoViewModel: InfoViewModel,
) {
    Crossfade(targetState = menzaId) { currentMenzaId ->
        when (LocalWindowWidth.current) {
            WindowSizeClass.COMPACT -> {
                InfoLayoutCompact(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    menzaId = currentMenzaId,
                    viewModel = infoViewModel,
                )
            }
            WindowSizeClass.MEDIUM -> {
                InfoLayoutMedium(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    menzaId = currentMenzaId,
                    viewModel = infoViewModel,
                )
            }
            WindowSizeClass.EXPANDED -> {
                InfoLayoutExpanded(
                    navController = navController,
                    snackbarHostState = snackbarHostState,
                    menzaId = currentMenzaId,
                    viewModel = infoViewModel,
                )
            }
        }
    }
}

@Composable
fun InfoLayoutCompact(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
) {
    InfoAllTogether(
        navController = navController,
        snackbarHost = snackbarHostState,
        menzaId = menzaId,
        viewModel = viewModel,
        Modifier.fillMaxSize(),
    )
}

@Composable
fun InfoLayoutMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
) {
    InfoAllTogether(
        navController = navController,
        snackbarHost = snackbarHostState,
        menzaId = menzaId,
        viewModel = viewModel,
        Modifier.fillMaxSize(),
    )
}

@Composable
fun InfoLayoutExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
) {
    UseSplitLayout(
        panel1 = {
            InfoPrimary(
                navController = navController,
                snackbarHost = snackbarHostState,
                menzaId = menzaId,
                viewModel = viewModel,
                Modifier.fillMaxSize()
            )
        },
        panel2 = {
            InfoSecondary(
                menzaId = menzaId,
                snackbarHost = snackbarHostState,
                viewModel = viewModel, Modifier.fillMaxSize()
            )
        }
    )
}