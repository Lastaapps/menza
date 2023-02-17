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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.newRoot
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
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
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsNode
import cz.lastaapps.menza.features.today.ui.navigation.TodayNode
import cz.lastaapps.menza.features.week.ui.node.WeekNode
import org.koin.androidx.compose.koinViewModel

class MainNode(
    buildContext: BuildContext,
    private val backStack: BackStack<MainNavType> = BackStack(
        initialElement = TodayNav,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<MainNavType>(backStack, buildContext) {

    @OptIn(ExperimentalMaterial3Api::class)
    private var currentDrawerState: DrawerState? = null

    private val onOsturak = { backStack.push(OsturakNav) }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun resolve(navTarget: MainNavType, buildContext: BuildContext): Node =
        when (navTarget) {
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

            SettingsNav -> SettingsNode(buildContext)

            OsturakNav -> OsturakNode(buildContext)

            LicenseNoticesNav -> LicenseNode(buildContext)

            PrivacyPolicyNav -> PrivacyNode(
                buildContext,
                onDismiss = { backStack.pop() },
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun View(modifier: Modifier) {
        val mainViewModel: MainViewModel = koinViewModel()
        val state by mainViewModel.flowState
        HandleAppear(mainViewModel)
        if (!state.isReady) return

        val drawerInitial = if (state.selectedMenza == null) Open else Closed
        val drawerState = rememberDrawerState(drawerInitial)
        LaunchedEffect(drawerState) {
            currentDrawerState = drawerState
        }

        val hostState = remember { SnackbarHostState() }

        val elements by backStack.elements.collectAsStateWithLifecycle()

        MainScreen(
            currentDest = elements.last().key.navTarget,
            drawerState = drawerState,
            settingsEverOpened = state.settingsViewed,
            hostState = hostState,
            selectedMenza = state.selectedMenza,
            onNavItemTopBar = { backStack.push(it) },
            onNavItemRoot = { backStack.newRoot(it) },
            drawerContent = {
                PermanentChild(navTarget = DrawerContent)
            },
            content = {
                Children(
                    navModel = backStack,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
