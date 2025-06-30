/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import cz.lastaapps.menza.ui.locals.FoldingClass
import cz.lastaapps.menza.ui.locals.LocalFoldProvider
import cz.lastaapps.menza.ui.locals.LocalSplitPosition
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding

private val railWidth = 80.dp

@Composable
fun MenzaScaffold(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    bottomBar: @Composable () -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    isFlip: Boolean,
    modifier: Modifier = Modifier,
    windowWidth: WindowWidthSizeClass = LocalWindowWidth.current,
    foldingFeature: FoldingClass = LocalFoldProvider.current,
    content: @Composable () -> Unit,
) {
    when (windowWidth) {
        WindowWidthSizeClass.Compact ->
            AppLayoutCompact(
                drawerState = drawerState,
                alternativeNavigation = alternativeNavigation,
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
                alternativeNavigation = alternativeNavigation,
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
                alternativeNavigation = alternativeNavigation,
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
    alternativeNavigation: Boolean,
    isFlip: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val topBarState =
        remember {
            TopBarState(
                alignRail = false,
                enableHamburger = true,
                enableRotation = true,
            )
        }

    MenzaModalDrawer(
        drawerState = drawerState,
        alternativeNavigation = alternativeNavigation,
        drawerContent = {
            Scaffold(
                snackbarHost = snackbarHost,
            ) { insets ->
                Box(
                    Modifier
                        .padding(insets)
                        .padding(vertical = Padding.Large),
                ) {
                    drawerContent()
                }
            }
        },
        modifier = modifier,
    ) {
        if (!isFlip) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = { topBar(topBarState) },
                bottomBar = bottomBar,
                snackbarHost = snackbarHost,
            ) { insets ->
                Box(
                    Modifier
                        .padding(insets)
                        .fillMaxSize(),
                ) {
                    content()
                }
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = snackbarHost,
            ) { insets ->
                Box(
                    Modifier
                        .padding(insets)
                        .fillMaxSize(),
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun AppLayoutMedium(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val topBarState =
        remember {
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
            modifier =
                Modifier
                    .padding(
                        top = insets.calculateTopPadding(),
                        bottom = insets.calculateBottomPadding(),
                    )
                    .fillMaxSize(),
            rail = rail,
        ) {
            MenzaDismissibleDrawer(
                drawerState = drawerState,
                alternativeNavigation = alternativeNavigation,
                modifier = Modifier.fillMaxSize(),
                drawerContent = drawerContent,
            ) {
                BoxWithConstraints(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            end = insets.calculateRightPadding(LocalLayoutDirection.current),
                        ),
                ) {
                    val padding = Padding.More.Screen
                    val totalWidthAvailable = maxWidth - padding
                    val startWidth = totalWidthAvailable * 0.5f
                    val endWidth = totalWidthAvailable * 0.5f

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, padding, endWidth),
                    ) { content() }
                }
            }
        }
    }
}

@Composable
private fun AppLayoutExpanded(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    foldingFeature: FoldingClass = LocalFoldProvider.current,
    content: @Composable () -> Unit,
) {
    if (foldingFeature is FoldingClass.Supported) {
        AppLayoutExpandedFold(
            drawerState = drawerState,
            alternativeNavigation = alternativeNavigation,
            snackbarHost = snackbarHost,
            topBar = topBar,
            rail = rail,
            drawerContent = drawerContent,
            content = content,
            modifier = modifier,
            foldingFeature = foldingFeature,
        )
    } else {
        AppLayoutExpandedNoFold(
            drawerState = drawerState,
            alternativeNavigation = alternativeNavigation,
            snackbarHost = snackbarHost,
            topBar = topBar,
            rail = rail,
            drawerContent = drawerContent,
            content = content,
            modifier = modifier,
        )
    }
}

@Composable
private fun AppLayoutExpandedNoFold(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val topBarState =
        remember {
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
            modifier =
                Modifier
                    .padding(
                        top = insets.calculateTopPadding(),
                        bottom = insets.calculateBottomPadding(),
                    ).fillMaxSize(),
            rail = rail,
        ) {
            MenzaDismissibleDrawer(
                drawerState = drawerState,
                alternativeNavigation = alternativeNavigation,
                drawerContent = drawerContent,
            ) {
                BoxWithConstraints(
                    Modifier
                        .fillMaxSize()
                        .padding(
                            end = insets.calculateRightPadding(LocalLayoutDirection.current),
                        ),
                ) {
                    val padding = Padding.More.Screen
                    val totalWidthAvailable = maxWidth - padding
                    val startWidth = totalWidthAvailable * 0.5f
                    val endWidth = totalWidthAvailable * 0.5f

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, padding, endWidth),
                    ) { content() }
                }
            }
        }
    }
}

@Composable
private fun AppLayoutExpandedFold(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    snackbarHost: @Composable () -> Unit,
    topBar: @Composable (TopBarState) -> Unit,
    rail: @Composable () -> Unit,
    drawerContent: @Composable () -> Unit,
    foldingFeature: FoldingClass.Supported,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val topBarState =
        remember {
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
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = insets.calculateTopPadding(),
                        bottom = insets.calculateBottomPadding(),
                    ),
            rail = rail,
        ) {
            val density = LocalDensity.current
            val weightStart = 0.5f
            val weightEnd = 0.5f
            val spacesWidth =
                remember(density, foldingFeature) {
                    @Suppress("ComplexRedundantLet")
                    with(density) {
                        foldingFeature.foldingFeature.bounds
                            .width()
                            .toDp()
                    }.let { max(it, Padding.More.Screen) }
                }

            MenzaDismissibleDrawer(
                drawerState = drawerState,
                alternativeNavigation = alternativeNavigation,
                drawerContent = drawerContent,
            ) {
                BoxWithConstraints(
                    Modifier
                        .padding(
                            end = insets.calculateRightPadding(LocalLayoutDirection.current),
                        )
                        .fillMaxSize(),
                ) {
                    val totalWidthAvailable = maxWidth - spacesWidth + railWidth
                    val startWidth = totalWidthAvailable * weightStart - railWidth
                    val endWidth = totalWidthAvailable * weightEnd

                    CompositionLocalProvider(
                        LocalSplitPosition provides Triple(startWidth, spacesWidth, endWidth),
                    ) { content() }
                }
            }
        }
    }
}
