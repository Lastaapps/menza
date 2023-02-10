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

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.DrawerValue.Open
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
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
import cz.lastaapps.menza.features.main.ui.screen.MenzaSelectionScreen
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class MainNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<MainNavType> = Spotlight(
        items = MainNavType.allTypes,
        // backPressHandler = TODO(),
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<MainNavType>(spotlight, buildContext) {
    override fun resolve(navTarget: MainNavType, buildContext: BuildContext): Node =
        node(buildContext) {
            Text(text = navTarget.toString())
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
                MenzaSelectionScreen(
                    onEdit = {},
                    onMenzaSelected = {
                        scope.launch {
                            when (drawerState.targetValue) {
                                Closed -> Open
                                Open -> Closed
                            }.let { drawerState.animateTo(it, spring()) }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            },
            content = {
                Children(
                    navModel = navModel,
                    modifier = Modifier.fillMaxSize(),
                )
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
