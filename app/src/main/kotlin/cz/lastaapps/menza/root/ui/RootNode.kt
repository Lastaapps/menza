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

package cz.lastaapps.menza.root.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.activate
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightFader
import cz.lastaapps.menza.root.ui.RootNavType.Loading
import cz.lastaapps.menza.root.ui.RootNavType.Main
import cz.lastaapps.menza.root.ui.RootNavType.SetupFlow
import cz.lastaapps.menza.starting.ui.StartingNode
import org.koin.androidx.compose.koinViewModel

internal class RootNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<RootNavType> = Spotlight(
        RootNavType.types,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val onDecided: () -> Unit,
) : ParentNode<RootNavType>(
    buildContext = buildContext,
    navModel = spotlight,
) {
    override fun resolve(navTarget: RootNavType, buildContext: BuildContext): Node {
        return when (navTarget) {
            Loading -> node(buildContext) {} // Splash screen will be shown
            SetupFlow -> StartingNode(buildContext)
            Main -> node(buildContext) {} // Nothing for now
        }
    }

    private fun indexOfType(type: RootNavType) =
        RootNavType.types.indexOf(type)

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: RootViewModel = koinViewModel()

        LaunchedEffect(viewModel) {
            viewModel.appeared()
        }
        val isReady by viewModel.isReady.collectAsStateWithLifecycle()
        val isSetup by viewModel.isSetUp.collectAsStateWithLifecycle()

        LaunchedEffect(isReady, isSetup) {
            if (isReady) {
                (if (isSetup) Main else SetupFlow)
                    .let { spotlight.activate(indexOfType(it)) }
            }
        }

        val activeIndex by spotlight.activeIndex().collectAsStateWithLifecycle(-1)

        Children(
            navModel = spotlight,
            modifier = modifier.onPlaced {
                if (indexOfType(Loading) != activeIndex) {
                    onDecided()
                }
            },
            transitionHandler = rememberSpotlightFader(),
        )
    }
}
