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

package cz.lastaapps.menza.features.root.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightFader
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.navigation.MainNode
import cz.lastaapps.menza.features.root.ui.RootNavType.Loading
import cz.lastaapps.menza.features.root.ui.RootNavType.Main
import cz.lastaapps.menza.features.root.ui.RootNavType.SetupFlow
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNode
import cz.lastaapps.menza.ui.util.activateType
import cz.lastaapps.menza.ui.util.indexOfType
import org.koin.androidx.compose.koinViewModel

internal class RootNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<RootNavType> = Spotlight(
        RootNavType.types,
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = DontHandleBackPress(),
    ),
    private val onDecided: () -> Unit,
) : ParentNode<RootNavType>(
    buildContext = buildContext,
    navModel = spotlight,
) {
    override fun resolve(navTarget: RootNavType, buildContext: BuildContext): Node {
        return when (navTarget) {
            Loading -> node(buildContext) {} // Splash screen will be shown
            SetupFlow -> StartingNode(buildContext, { spotlight.activateType(Main) })
            Main -> MainNode(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: RootViewModel = koinViewModel()

        HandleAppear(viewModel)
        val state by viewModel.flowState

        LaunchedEffect(state.isReady, state.isSetUp) {
            if (state.isReady) {
                (if (state.isSetUp) Main else SetupFlow)
                    .let { spotlight.activateType(it) }
            }
        }

        ApplyAppTheme(viewModel) {
            val activeIndex by spotlight.activeIndex().collectAsStateWithLifecycle(-1)

            Children(
                navModel = spotlight,
                modifier = modifier.onPlaced {
                    if (spotlight.indexOfType(Loading) != activeIndex) {
                        onDecided()
                    }
                },
                transitionHandler = rememberSpotlightFader(),
            )
        }
    }
}