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

package cz.lastaapps.menza.features.main.ui.layout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.ui.root.locals.FoldingClass
import cz.lastaapps.menza.ui.root.locals.LocalFoldProvider
import cz.lastaapps.menza.ui.root.locals.LocalSplitPosition
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.coroutines.launch

private val railWidth = 80.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaScaffold(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    bottomBar: @Composable () -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowWidth: WindowWidthSizeClass = LocalWindowWidth.current,
    foldingFeature: FoldingClass = LocalFoldProvider.current,
) {
    when (windowWidth) {
        WindowWidthSizeClass.Compact ->
            AppLayoutCompact(
                drawerState = drawerState,
                snackbarHost = snackbarHost,
                topBar = topBar,
                bottomBar = bottomBar,
                drawerContent = drawerContent,
                content = content,
                modifier = modifier,
            )

        WindowWidthSizeClass.Medium ->
            AppLayoutMedium(
                drawerState = drawerState,
                snackbarHost = snackbarHost,
                topBar = topBar,
                rail = rail,
                drawerContent = drawerContent,
                content = content,
                modifier = modifier,
            )

        WindowWidthSizeClass.Expanded ->
            AppLayoutExpanded(
                drawerState = drawerState,
                snackbarHost = snackbarHost,
                topBar = topBar,
                rail = rail,
                drawerContent = drawerContent,
                content = content,
                modifier = modifier,
                foldingFeature = foldingFeature,
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLayoutCompact(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    bottomBar: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = remember {
        TopBarState(
            alignRail = false,
            enableHamburger = true,
            enableRotation = true,
        )
    }

    val scope = rememberCoroutineScope()
    MenzaModalDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(Modifier.padding(vertical = MenzaPadding.Large)) {
                drawerContent()
            }
        },
        modifier = modifier,
    ) {
        Scaffold(
            Modifier.fillMaxSize(),
            topBar = { topBar(topBarState) },
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
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
private fun AppLayoutMedium(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = remember {
        TopBarState(
            alignRail = true,
            enableHamburger = true,
            enableRotation = false,
        )
    }

    Scaffold(
        topBar = { topBar(topBarState) },
        snackbarHost = snackbarHost,
        modifier = modifier,
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = rail,
        ) {
            MenzaDismissibleDrawer(
                drawerState = drawerState,
                modifier = Modifier.fillMaxSize(),
                drawerContent = drawerContent,
                content = content,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLayoutExpanded(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    foldingFeature: FoldingClass = LocalFoldProvider.current,
) {
    if (foldingFeature is FoldingClass.Supported)
        AppLayoutExpandedFold(
            drawerState = drawerState,
            snackbarHost = snackbarHost,
            topBar = topBar,
            rail = rail,
            drawerContent = drawerContent,
            content = content,
            modifier = modifier,
            foldingFeature = foldingFeature,
        )
    else
        AppLayoutExpandedNoFold(
            drawerState = drawerState,
            snackbarHost = snackbarHost,
            topBar = topBar,
            rail = rail,
            drawerContent = drawerContent,
            content = content,
            modifier = modifier,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLayoutExpandedNoFold(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val topBarState = remember {
        TopBarState(
            alignRail = true,
            enableHamburger = false,
            enableRotation = false,
        )
    }

    Scaffold(
        topBar = { topBar(topBarState) },
        snackbarHost = snackbarHost,
        modifier = modifier,
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = rail,
        ) {
            MenzaDismissibleDrawer(
                drawerState = drawerState,
                drawerContent = drawerContent,
            ) {
                BoxWithConstraints(
                    Modifier.fillMaxSize()
                ) {
                    val totalWidthAvailable = maxWidth
                    val startWidth = totalWidthAvailable * 0.5f
                    val endWidth = totalWidthAvailable * 0.5f

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, 0.dp, endWidth)
                    ) { content() }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppLayoutExpandedFold(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    foldingFeature: FoldingClass.Supported,
    modifier: Modifier = Modifier,
) {
    val topBarState = remember {
        TopBarState(
            alignRail = true,
            enableHamburger = false,
            enableRotation = false,
        )
    }

    Scaffold(
        topBar = { topBar(topBarState) },
        snackbarHost = snackbarHost,
        modifier = modifier,
    ) { insets ->
        MenzaDismissibleDrawerWithRailLayout(
            Modifier
                .padding(insets)
                .fillMaxSize(),
            rail = rail,
        ) {

            val density = LocalDensity.current
            val weightStart = 0.5f
            val weightEnd = 0.5f
            val spacesWidth = remember(density, foldingFeature) {
                with(density) { foldingFeature.foldingFeature.bounds.width().toDp() }
            }

            MenzaDismissibleDrawer(
                drawerState = drawerState,
                drawerContent = drawerContent,
            ) {
                BoxWithConstraints(
                    Modifier.fillMaxSize(),
                ) {
                    val totalWidthAvailable = maxWidth - spacesWidth + railWidth
                    val startWidth = totalWidthAvailable * weightStart - railWidth
                    val endWidth = totalWidthAvailable * weightEnd

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, spacesWidth, endWidth)
                    ) { content() }
                }
            }
        }
    }
}

@Composable
fun SplitLayout(
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        val size = LocalSplitPosition.current
        Box(
            Modifier.width(size.first)
        ) { panel1() }

        Spacer(Modifier.width(size.second))

        Box(
            Modifier.width(size.third)
        ) { panel2() }
    }
}
