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

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.window.layout.FoldingFeature
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.FoldingClass
import cz.lastaapps.menza.ui.LocalFoldProvider
import cz.lastaapps.menza.ui.main.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutCompact(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    enableIcon: Boolean,
    showHamburgerMenu: Boolean, //or the back arrow
    onMenuButtonClicked: () -> Unit,
    content: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    val scope = rememberCoroutineScope()
    MenzaNavDrawer(
        selectedMenza = menzaId,
        onMenzaSelected = {
            scope.launch { drawerState.close() }
            onMenzaSelected(it)
        },
        drawerState,
    ) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                val icon = if (enableIcon)
                    if (showHamburgerMenu) Icons.Default.Menu else Icons.Default.ArrowBack
                else null
                val rotated = drawerState.isOpen

                MainTopBar(
                    menzaName = menza?.name,
                    menuIcon = icon,
                    menuDescription = null,
                    menuRotated = rotated,
                    onMenuClicked = onMenuButtonClicked,
                )
            },
            bottomBar = {
                MainBottomNav(navController)
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { insets ->
            Box(
                Modifier
                    .padding(insets)
                    .fillMaxSize()
            ) {
                content()

                BackHandler(drawerState.isOpen) {
                    scope.launch { drawerState.close() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutMedium(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                menzaName = menza?.name,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        Row(
            Modifier
                .padding(insets)
                .fillMaxSize()
        ) {
            MainNavRail(navController)
            Column(Modifier.fillMaxHeight()) {
                var expanded by rememberSaveable() { mutableStateOf(false) }
                MenzaList(
                    modifier = Modifier.weight(1f),
                    selectedMenza = menzaId,
                    onMenzaSelected = {
                        onMenzaSelected(it)
                        scope.launch { drawerState.close() }
                    },
                    expanded = expanded,
                    menzaListViewModel = hiltViewModel(),
                )
                IconButton(onClick = { expanded = !expanded }) {
                    val icon = if (expanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward
                    Icon(icon, contentDescription = null)
                }
            }

            Box(Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutExpandedSimple(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                menzaName = menza?.name,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        Row(
            Modifier
                .padding(insets)
                .fillMaxSize()
        ) {
            MainNavRail(navController)
            Column(Modifier.fillMaxHeight()) {
                var expanded by rememberSaveable() { mutableStateOf(false) }
                MenzaList(
                    modifier = Modifier.weight(1f),
                    selectedMenza = menzaId,
                    onMenzaSelected = {
                        onMenzaSelected(it)
                        scope.launch { drawerState.close() }
                    },
                    expanded = expanded,
                    menzaListViewModel = hiltViewModel(),
                )
                IconButton(onClick = { expanded = !expanded }) {
                    val icon = if (expanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward
                    Icon(icon, contentDescription = null)
                }
            }

            Box(Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutExpanded(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    val scope = rememberCoroutineScope()
    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            MainTopBar(
                menzaName = menza?.name,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        Row(
            Modifier
                .padding(insets)
                .fillMaxSize()
        ) {
            var weightStart = 0.66f
            var weightEnd = 0.33f
            var spacesWidth = 0.dp

            val foldingFeature = LocalFoldProvider.current
            if (foldingFeature is FoldingClass.Supported) {
                val feature = foldingFeature.foldingFeature
                if (feature.orientation == FoldingFeature.Orientation.VERTICAL && feature.isSeparating) {
                    weightStart = 0.5f
                    weightEnd = 0.5f
                    with(LocalDensity.current) {
                        spacesWidth = feature.bounds.width().toDp()
                    }
                }
            }

            Row(Modifier.weight(weightStart)) {
                MainNavRail(navController)
                Column(Modifier.fillMaxHeight()) {
                    var expanded by rememberSaveable() { mutableStateOf(false) }
                    MenzaList(
                        modifier = Modifier.weight(1f),
                        selectedMenza = menzaId,
                        onMenzaSelected = {
                            onMenzaSelected(it)
                            scope.launch { drawerState.close() }
                        },
                        expanded = expanded,
                        menzaListViewModel = hiltViewModel(),
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        val icon =
                            if (expanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward
                        Icon(icon, contentDescription = null)
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    panel1()
                }
            }

            //used to remove content from the hing area
            Spacer(modifier = Modifier.width(spacesWidth))

            Row(Modifier.weight(weightEnd)) {
                Box(modifier = Modifier.fillMaxSize()) {
                    panel2()
                }
            }
        }
    }
}

