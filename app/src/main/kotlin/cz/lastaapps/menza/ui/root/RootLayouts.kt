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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.FoldingClass
import cz.lastaapps.menza.ui.LocalFoldProvider
import cz.lastaapps.menza.ui.layout.main.MainBottomNav
import cz.lastaapps.menza.ui.layout.main.MainNavRail
import cz.lastaapps.menza.ui.layout.main.MainTopBar
import cz.lastaapps.menza.ui.layout.main.MenzaNavDrawer
import cz.lastaapps.menza.ui.layout.menza.MenzaList
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import kotlinx.coroutines.launch

val sidesPadding = 16.dp

@Suppress("UNUSED_PARAMETER")
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
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
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
        menzaListViewModel = menzaViewModel,
    ) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = {
                val icon = if (enableIcon)
                    if (showHamburgerMenu) Icons.Default.Menu else Icons.Default.ArrowBack
                else null
                val rotated = drawerState.isOpen

                MainTopBar(
                    navController = navController,
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
                    .padding(sidesPadding)
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
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackButton: Boolean,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    onBackButtonPressed: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            if (!showBackButton)
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                )
            else
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                    menuIcon = Icons.Default.ArrowBack,
                    menuDescription = "Go back",
                    onMenuClicked = onBackButtonPressed,
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
            MenzaListExpandable(
                drawerState = drawerState,
                expanded = expanded,
                onClick = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel
            )

            Box(
                Modifier
                    .padding(sidesPadding)
                    .fillMaxSize()
            ) {
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
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackButton: Boolean,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    onBackButtonPressed: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            if (!showBackButton)
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                )
            else
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                    menuIcon = Icons.Default.ArrowBack,
                    menuDescription = "Go back",
                    onMenuClicked = onBackButtonPressed,
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
            MenzaListExpandable(
                drawerState = drawerState,
                expanded = expanded,
                onClick = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel
            )

            Box(
                Modifier
                    .padding(sidesPadding)
                    .fillMaxSize()
            ) {
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
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackButton: Boolean,
    onBackButtonPressed: () -> Unit = {},
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val foldingFeature = LocalFoldProvider.current
    if (foldingFeature is FoldingClass.Supported)
        AppLayoutExpandedFold(
            navController = navController,
            menzaId = menzaId,
            onMenzaSelected = onMenzaSelected,
            menzaViewModel = menzaViewModel,
            snackbarHostState = snackbarHostState,
            drawerState = drawerState,
            showBackButton = showBackButton,
            onBackButtonPressed = onBackButtonPressed,
            expanded = expanded,
            onExpandedClicked = onExpandedClicked,
            panel1 = panel1,
            panel2 = panel2,
        )
    else
        AppLayoutExpandedNoFold(
            navController = navController,
            menzaId = menzaId,
            onMenzaSelected = onMenzaSelected,
            menzaViewModel = menzaViewModel,
            snackbarHostState = snackbarHostState,
            drawerState = drawerState,
            showBackButton = showBackButton,
            onBackButtonPressed = onBackButtonPressed,
            expanded = expanded,
            onExpandedClicked = onExpandedClicked,
            panel1 = panel1,
            panel2 = panel2,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLayoutExpandedNoFold(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackButton: Boolean,
    onBackButtonPressed: () -> Unit = {},
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            if (!showBackButton)
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                )
            else
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                    menuIcon = Icons.Default.ArrowBack,
                    menuDescription = "Go back",
                    onMenuClicked = onBackButtonPressed,
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

            MenzaListExpandable(
                drawerState = drawerState,
                expanded = expanded,
                onClick = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
            )

            Box(
                modifier = Modifier
                    .padding(
                        top = sidesPadding,
                        bottom = sidesPadding,
                        start = sidesPadding,
                        end = sidesPadding / 2,
                    )
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                panel1()
            }

            Box(
                modifier = Modifier
                    .padding(
                        top = sidesPadding,
                        bottom = sidesPadding,
                        start = sidesPadding / 2,
                        end = sidesPadding,
                    )
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                panel2()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutExpandedFold(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackButton: Boolean,
    onBackButtonPressed: () -> Unit = {},
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            if (!showBackButton)
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                )
            else
                MainTopBar(
                    navController = navController,
                    menzaName = menza?.name,
                    menuIcon = Icons.Default.ArrowBack,
                    menuDescription = "Go back",
                    onMenuClicked = onBackButtonPressed,
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
            val foldingFeature = LocalFoldProvider.current as FoldingClass.Supported

            val weightStart = 0.5f
            val weightEnd = 0.5f
            val spacesWidth = with(LocalDensity.current) {
                foldingFeature.foldingFeature.bounds.width().toDp()
            }

            Row(Modifier.weight(weightStart)) {
                MainNavRail(navController)

                MenzaListExpandable(
                    drawerState = drawerState,
                    expanded = expanded,
                    onClick = onExpandedClicked,
                    menzaId = menzaId,
                    onMenzaSelected = onMenzaSelected,
                    menzaViewModel = menzaViewModel,
                )

                Box(
                    modifier = Modifier
                        .padding(
                            top = sidesPadding,
                            bottom = sidesPadding,
                            start = sidesPadding,
                            end = sidesPadding / 2,
                        )
                        .fillMaxSize()
                ) {
                    panel1()
                }
            }

            //used to remove content from the hing area
            Spacer(modifier = Modifier.width(spacesWidth))

            Box(Modifier.weight(weightEnd)) {
                Box(
                    modifier = Modifier
                        .padding(
                            top = sidesPadding,
                            bottom = sidesPadding,
                            start = sidesPadding / 2,
                            end = sidesPadding,
                        )
                        .fillMaxSize()
                ) {
                    panel2()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenzaListExpandable(
    drawerState: DrawerState,
    expanded: Boolean,
    onClick: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
) {
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxHeight()) {
        MenzaList(
            modifier = Modifier.weight(1f),
            selectedMenza = menzaId,
            onMenzaSelected = {
                onMenzaSelected(it)
                scope.launch { drawerState.close() }
            },
            expanded = expanded,
            menzaListViewModel = menzaViewModel,
        )
        val rotation by animateFloatAsState(if (expanded) 0f else 180f)
        IconButton(onClick = onClick, Modifier.rotate(rotation)) {
            Icon(Icons.Default.ArrowBack, contentDescription = null)
        }
    }
}
