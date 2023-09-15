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

package cz.lastaapps.menza.features.main.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.DrawerValue.Open
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.newRoot
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.info.ui.node.InfoNode
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.DrawerContent
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.InfoNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.LicenseNoticesNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.OsturakNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.PrivacyPolicyNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.SettingsNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.TodayNav
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.WeekNav
import cz.lastaapps.menza.features.main.ui.node.DrawerNode
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import cz.lastaapps.menza.features.other.ui.node.LicenseNode
import cz.lastaapps.menza.features.other.ui.node.OsturakNode
import cz.lastaapps.menza.features.other.ui.node.PrivacyNode
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsHubNode
import cz.lastaapps.menza.features.today.ui.navigation.TodayNode
import cz.lastaapps.menza.features.week.ui.node.WeekNode
import cz.lastaapps.menza.ui.locals.LocalMayBeFlipCover
import cz.lastaapps.menza.ui.util.active
import cz.lastaapps.menza.ui.util.nodeViewModel
import org.koin.androidx.compose.koinViewModel

class MainNode(
    buildContext: BuildContext,
    private val backStack: BackStack<MainNavType> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(TodayNav),
            savedStateMap = buildContext.savedStateMap,
        ),
        motionController = { BackStackFader(it) },
    ),
) : ParentNode<MainNavType>(backStack, buildContext) {

    private var currentDrawerState: DrawerState? = null

    private val onOsturak = { backStack.push(OsturakNav) }

    override fun resolve(interactionTarget: MainNavType, buildContext: BuildContext): Node =
        when (interactionTarget) {
            DrawerContent -> DrawerNode(buildContext, ::currentDrawerState)

            TodayNav -> TodayNode(
                buildContext = buildContext,
                onOsturak = onOsturak,
            )

            WeekNav -> WeekNode(
                buildContext = buildContext,
                onOsturak = onOsturak,
            )

            InfoNav -> InfoNode(
                buildContext = buildContext,
                onOsturak = onOsturak,
            )

            SettingsNav -> SettingsHubNode(buildContext)

            OsturakNav -> OsturakNode(buildContext)

            LicenseNoticesNav -> LicenseNode(buildContext)

            PrivacyPolicyNav -> PrivacyNode(
                buildContext,
                onDismiss = { backStack.pop() },
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        val mainViewModel: MainViewModel = nodeViewModel()
        val state by mainViewModel.flowState
        HandleAppear(mainViewModel)
        if (!state.isReady) return

        val drawerInitial = if (state.selectedMenza == null) Open else Closed
        val drawerState = rememberDrawerState(drawerInitial)
        LaunchedEffect(drawerState) {
            currentDrawerState = drawerState
        }

        val hostState = remember { SnackbarHostState() }

        val active by remember(backStack) {
            backStack.active()
        }.collectAsStateWithLifecycle(initialValue = null)

        MainScreen(
            currentDest = active,
            drawerState = drawerState,
            settingsEverOpened = state.settingsViewed,
            hostState = hostState,
            selectedMenza = state.selectedMenza,
            isFlip = state.isFlip && LocalMayBeFlipCover.current,
            onNavItemTopBar = { backStack.push(it) },
            onNavItemRoot = {
                // TodayNav is always at the bottom
                if (it == TodayNav) {
                    backStack.newRoot(TodayNav)
                } else {
                    backStack.newRoot(TodayNav, mode = Operation.Mode.IMMEDIATE)
                    backStack.push(it) // )
                }
            },
            drawerContent = {
                PermanentChild(DrawerContent)
            },
            content = {
                AppyxComponent(
                    appyxComponent = backStack,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
