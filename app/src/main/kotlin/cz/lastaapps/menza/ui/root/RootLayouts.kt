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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.FoldingClass
import cz.lastaapps.menza.ui.LocalFoldProvider
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.layout.main.*
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import kotlinx.coroutines.launch

val sidesPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutCompact(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    val scope = rememberCoroutineScope()
    MenzaModalDrawer(
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
                DefaultTopBar(
                    navController = navController, drawerState = drawerState,
                    menza = menza,
                    alignRail = false,
                    enableHamburger = true,
                    showBackArrow, onBackArrowClick,
                    enableRotation = true,
                )
            },
            bottomBar = {
                MainBottomNav(navController, settingsViewModel)
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
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            DefaultTopBar(
                navController = navController, drawerState = drawerState,
                menza = menza,
                alignRail = true,
                enableHamburger = true,
                showBackArrow, onBackArrowClick,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = { MainNavRail(navController, settingsViewModel) },
        ) {
            MenzaDismissibleDrawer(
                selectedMenza = menzaId,
                onMenzaSelected = onMenzaSelected,
                drawerState = drawerState,
                menzaListViewModel = menzaViewModel,
                modifier = Modifier
                    .padding(sidesPadding)
                    .fillMaxSize(),
                content = content,
            )
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
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            DefaultTopBar(
                navController = navController, drawerState = drawerState,
                menza = menza,
                alignRail = true,
                enableHamburger = true,
                showBackArrow, onBackArrowClick,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = { MainNavRail(navController, settingsViewModel) }
        ) {
            MenzaDismissibleDrawer(
                selectedMenza = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaListViewModel = menzaViewModel,
                drawerState = drawerState,
                modifier = Modifier
                    .padding(sidesPadding)
                    .fillMaxSize(),
                content = content,
            )
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
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
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
            settingsViewModel = settingsViewModel,
            snackbarHostState = snackbarHostState,
            drawerState = drawerState,
            showBackArrow = showBackArrow,
            onBackArrowClick = onBackArrowClick,
            panel1 = panel1,
            panel2 = panel2,
        )
    else
        AppLayoutExpandedNoFold(
            navController = navController,
            menzaId = menzaId,
            onMenzaSelected = onMenzaSelected,
            menzaViewModel = menzaViewModel,
            settingsViewModel = settingsViewModel,
            snackbarHostState = snackbarHostState,
            drawerState = drawerState,
            showBackArrow = showBackArrow,
            onBackArrowClick = onBackArrowClick,
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
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            DefaultTopBar(
                navController = navController, drawerState = drawerState,
                menza = menza,
                alignRail = true,
                enableHamburger = true,
                showBackArrow, onBackArrowClick,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = { MainNavRail(navController, settingsViewModel) })
        {
            MenzaDismissibleDrawer(
                selectedMenza = menzaId,
                onMenzaSelected = onMenzaSelected,
                drawerState = drawerState,
                menzaListViewModel = menzaViewModel,
            ) {
                Row(Modifier.fillMaxWidth()) {
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayoutExpandedFold(
    navController: NavController,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit = {},
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit
) {
    val menza = remember(menzaId) {
        menzaId?.let { menzaViewModel.getForId(menzaId) }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            DefaultTopBar(
                navController = navController, drawerState = drawerState,
                menza = menza,
                alignRail = true,
                enableHamburger = true,
                showBackArrow, onBackArrowClick,
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = { MainNavRail(navController, settingsViewModel) }
        ) {
            val foldingFeature = LocalFoldProvider.current as FoldingClass.Supported

            val density = LocalDensity.current
            val weightStart = 0.5f
            val weightEnd = 0.5f
            val spacesWidth = remember(density, foldingFeature) {
                with(density) { foldingFeature.foldingFeature.bounds.width().toDp() }
            }

            MenzaDismissibleDrawer(
                selectedMenza = menzaId,
                onMenzaSelected = onMenzaSelected,
                drawerState = drawerState,
                menzaListViewModel = menzaViewModel,
            ) {
                BoxWithConstraints {
                    val railWidth = 80.dp
                    val totalWidthAvailable =
                        with(LocalDensity.current) {
                            maxWidth.roundToPx().toDp()
                        } - spacesWidth + railWidth
                    val startWidth = totalWidthAvailable * weightStart - railWidth
                    val endWidth = totalWidthAvailable * weightEnd

                    Row {
                        Box(Modifier.width(startWidth)) { panel1() }
                        Spacer(Modifier.width(spacesWidth))
                        Box(Modifier.width(endWidth)) { panel2() }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(
    navController: NavController,
    drawerState: DrawerState,
    menza: Menza?,
    alignRail: Boolean,
    enableHamburger: Boolean,
    showBackArrow: Boolean,
    onBackArrowClick: () -> Unit,
    enableRotation: Boolean = false, // used for an animation while using modal Drawer
) {
    val icon = when {
        showBackArrow -> Icons.Default.ArrowBack
        enableHamburger -> Icons.Default.Menu
        else -> null
    }
    val description = when {
        showBackArrow -> stringResource(R.string.ui_top_bar_back_arrow)
        enableHamburger -> stringResource(R.string.ui_top_bar_show_menza_list)
        else -> null
    }
    val rotated =
        remember(drawerState.targetValue) { drawerState.targetValue == DrawerValue.Open && enableRotation }
    val scope = rememberCoroutineScope()

    MainTopBar(
        navController = navController,
        menzaName = menza?.name,
        menuIcon = icon,
        alignRail = alignRail,
        menuDescription = description,
        menuRotated = rotated,
        onMenuClicked = {
            if (showBackArrow) onBackArrowClick()
            else if (enableHamburger) {
                scope.launch {
                    if (drawerState.targetValue == DrawerValue.Open)
                        drawerState.close() else drawerState.open()
                }
            }
        },
    )
}