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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.init.InitDecision
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.WithLocalWindowSizes
import cz.lastaapps.menza.ui.WithSnackbarProvider
import cz.lastaapps.menza.ui.dish.DishContent
import cz.lastaapps.menza.ui.main.*
import cz.lastaapps.menza.ui.theme.AppTheme
import kotlinx.coroutines.launch

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
            InitDecision {
                AppContent()
            }
        }
    }
}

@Composable
private fun AppContent() {

    val snackbarHostState = remember { SnackbarHostState() }

    WithSnackbarProvider(snackbarHostState = snackbarHostState) {
        when (LocalWindowWidth.current) {
            WindowSizeClass.COMPACT -> AppContentCompact(snackbarHostState)
            WindowSizeClass.EXPANDED -> AppContentMedium(snackbarHostState)
            WindowSizeClass.MEDIUM -> AppContentExpanded(snackbarHostState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContentCompact(snackbarHostState: SnackbarHostState) {
    var menzaId by rememberSaveable {
        mutableStateOf<MenzaId?>(null)
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
    val scope = rememberCoroutineScope()
    MenzaNavDrawer(
        selectedMenza = menzaId,
        onMenzaSelected = {
            menzaId = it
            scope.launch { drawerState.close() }
        },
        drawerState,
    ) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                MainTopBar(
                    enableMenuIcon = true,
                    menuOpened = drawerState.targetValue == DrawerValue.Open,
                ) { scope.launch { drawerState.open() } }
            },
            bottomBar = { MainBottomNav() },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) {
            DishContent(Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppContentMedium(snackbarHostState: SnackbarHostState) {
    var menzaId by rememberSaveable {
        mutableStateOf<MenzaId?>(null)
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = { MainTopBar(enableMenuIcon = false, menuOpened = false) {} },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) {
        Row(Modifier.fillMaxSize()) {
            MainNavRail()
            Column(Modifier.fillMaxHeight()) {
                var expanded by rememberSaveable() { mutableStateOf(false) }
                MenzaList(
                    modifier = Modifier.weight(1f),
                    selectedMenza = menzaId,
                    onMenzaSelected = { menzaId = it },
                    expanded = expanded,
                    menzaListViewModel = hiltViewModel(),
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "")
                }
            }
            DishContent(Modifier.fillMaxSize())
        }
    }
}

@Composable
fun AppContentExpanded(snackbarHostState: SnackbarHostState) {
    AppContentMedium(snackbarHostState)
}


