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

package cz.lastaapps.menza.ui.root

import android.app.Activity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.init.InitDecision
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.ui.WithFoldingFeature
import cz.lastaapps.menza.ui.WithLocalWindowSizes
import cz.lastaapps.menza.ui.WithSnackbarProvider
import cz.lastaapps.menza.ui.info.InfoLayout
import cz.lastaapps.menza.ui.main.MenzaViewModel
import cz.lastaapps.menza.ui.settings.SettingsLayout
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.today.TodayDest
import cz.lastaapps.menza.ui.week.WeekLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot(
    activity: Activity,
    viewModel: RootViewModel,
) {
    if (!viewModel.isReady.collectAsState().value)
        return

    val state by viewModel.isDark.collectAsState()

    AppTheme(state) {
        WithLocalWindowSizes(activity = activity) {
            WithFoldingFeature(activity = activity) {
                ProvideWindowInsets {

                    //Download default data
                    InitDecision {
                        //show app if ready
                        AppContent()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContent(viewModel: MenzaViewModel = hiltViewModel()) {

    val menzaId by viewModel.selectedMenza.collectAsState()
    val onMenzaSelected: (MenzaId?) -> Unit = { viewModel.selectMenza(it) }

    val snackbarHostState = remember { SnackbarHostState() }
    val navHostState = rememberNavController()

    //drawer should auto open, if there is no menza selected and user hasn't touched the drawer
    val drawerState = rememberDrawerState(DrawerValue.Open)


    WithSnackbarProvider(snackbarHostState = snackbarHostState) {

        NavHost(
            navController = navHostState,
            startDestination = Dest.R.today,
            modifier = Modifier.fillMaxSize()
        ) {

            composable(
                Dest.R.today,
            ) {
                TodayDest(
                    navController = navHostState,
                    snackbarHostState = snackbarHostState,
                    drawerState = drawerState,
                    menzaId = menzaId,
                    onMenzaSelected = onMenzaSelected,
                    menzaViewModel = viewModel,
                    todayViewModel = hiltViewModel(),
                )
            }
            composable(
                Dest.R.week,
            ) {
                WeekLayout(
                    navController = navHostState,
                    snackbarHostState = snackbarHostState,
                    drawerState = drawerState,
                    menzaId = menzaId,
                    onMenzaSelected = onMenzaSelected,
                    menzaViewModel = viewModel,
                    weekViewModel = hiltViewModel(),
                )
            }
            composable(
                Dest.R.info,
            ) {
                InfoLayout(
                    navController = navHostState,
                    snackbarHostState = snackbarHostState,
                    drawerState = drawerState,
                    menzaId = menzaId,
                    onMenzaSelected = onMenzaSelected,
                    menzaViewModel = viewModel,
                    infoViewModel = hiltViewModel(),
                )
            }
            composable(
                Dest.R.settings,
            ) {
                SettingsLayout(
                    navController = navHostState,
                    snackbarHostState = snackbarHostState,
                    drawerState = drawerState,
                    menzaId = menzaId,
                    onMenzaSelected = onMenzaSelected,
                    menzaViewModel = viewModel,
                    settingsViewModel = hiltViewModel(),
                )
            }
        }
    }
}


