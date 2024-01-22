/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import cz.lastaapps.menza.ui.locals.FoldingClass
import cz.lastaapps.menza.ui.locals.LocalFoldProvider
import cz.lastaapps.menza.ui.locals.LocalSplitPosition
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding
import kotlinx.coroutines.launch

private val railWidth = 80.dp

@Composable
fun MenzaScaffold(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    bottomBar: @Composable () -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    isFlip: Boolean,
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
                isFlip = isFlip,
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

@Composable
private fun AppLayoutCompact(
    drawerState: DrawerState,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    bottomBar: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
    isFlip: Boolean,
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
            Box(Modifier.padding(vertical = Padding.Large)) {
                drawerContent()
            }
        },
        modifier = modifier,
    ) {
        if (!isFlip) {
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
                }
            }
        } else {
            content()
        }

        BackHandler(drawerState.isOpen) {
            scope.launch { drawerState.close() }
        }
    }
}

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
            ) {
                BoxWithConstraints(
                    Modifier.fillMaxSize()
                ) {
                    val padding = Padding.More.Screen
                    val totalWidthAvailable = maxWidth - padding
                    val startWidth = totalWidthAvailable * 0.5f
                    val endWidth = totalWidthAvailable * 0.5f

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, padding, endWidth)
                    ) { content() }
                }
            }
        }
    }
}

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
                    val padding = Padding.More.Screen
                    val totalWidthAvailable = maxWidth - padding
                    val startWidth = totalWidthAvailable * 0.5f
                    val endWidth = totalWidthAvailable * 0.5f

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, padding, endWidth)
                    ) { content() }
                }
            }
        }
    }
}

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
                @Suppress("ComplexRedundantLet")
                with(density) { foldingFeature.foldingFeature.bounds.width().toDp() }
                    .let { max(it, Padding.More.Screen) }
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
