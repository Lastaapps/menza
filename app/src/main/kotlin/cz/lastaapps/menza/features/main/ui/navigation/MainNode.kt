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
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.activate
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.DrawerContent
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.Today
import cz.lastaapps.menza.features.main.ui.navigation.MainNavType.Week
import cz.lastaapps.menza.features.main.ui.node.DrawerNode
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import cz.lastaapps.menza.features.today.ui.navigation.TodayNode
import cz.lastaapps.menza.features.week.ui.node.WeekNode
import cz.lastaapps.menza.ui.util.activateType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<MainNavType> = Spotlight(
        items = MainNavType.allMainTypes,
        // backPressHandler = TODO(),
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<MainNavType>(spotlight, buildContext) {

    @OptIn(ExperimentalMaterial3Api::class)
    private var currentDrawerState: DrawerState? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun resolve(navTarget: MainNavType, buildContext: BuildContext): Node =
        when (navTarget) {
            DrawerContent -> DrawerNode(buildContext, ::currentDrawerState)
            Today -> TodayNode(
                buildContext,
                onOsturak = { spotlight.activateType(MainNavType.Osturak) },
            )

            Week -> WeekNode(
                buildContext,
                onOsturak = { spotlight.activateType(MainNavType.Osturak) },
            )

            else ->
                node(buildContext) {
                    Text(text = navTarget.toString())
                }
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
        val scope = rememberCoroutineScope()

        val currentIndex by spotlight.activeIndex().collectAsStateWithLifecycle(0)
        val elements by spotlight.elements.collectAsStateWithLifecycle()

        MainScreen(
            currentDest = elements[currentIndex].key.navTarget,
            drawerState = drawerState,
            settingsEverOpened = state.settingsViewed,
            hostState = hostState,
            selectedMenza = state.selectedMenza,
            onNavItem = {
                scope.launch {
                    val elemList = spotlight.elements.first()
                        .map { it.key.navTarget }
                    spotlight.activate(elemList.indexOf(it))
                }
            },
            drawerContent = {
                PermanentChild(navTarget = DrawerContent)
            },
            content = {
                Children(
                    navModel = spotlight,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
