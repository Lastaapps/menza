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

package cz.lastaapps.menza.ui.dests.others.osturak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
import cz.lastaapps.menza.ui.root.AppLayoutMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsturakLayout(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
) {
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            OsturakLayoutCompact(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
            )
        }
        WindowSizeClass.MEDIUM -> {
            OsturakLayoutMedium(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
            )
        }
        WindowSizeClass.EXPANDED -> {
            OsturakLayoutExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsturakLayoutCompact(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
) {
    AppLayoutCompact(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = true,
        onBackArrowClick = { navController.navigateUp() },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OsturakText()
                OsturakImages(Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsturakLayoutMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
) {
    AppLayoutMedium(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = false,
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OsturakText()
                OsturakImages(Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OsturakLayoutExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        showBackArrow = false,
        panel1 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                OsturakText()
            }
        },
        panel2 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) {
                OsturakImages()
            }
        }
    )
}
