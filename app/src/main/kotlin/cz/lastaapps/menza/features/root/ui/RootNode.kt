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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.navigation.MainNode
import cz.lastaapps.menza.features.root.ui.RootNavType.LoadingNav
import cz.lastaapps.menza.features.root.ui.RootNavType.MainNav
import cz.lastaapps.menza.features.root.ui.RootNavType.SetupFlowNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNode
import cz.lastaapps.menza.ui.util.activateItem
import cz.lastaapps.menza.ui.util.activeIndex
import cz.lastaapps.menza.ui.util.indexOfType
import cz.lastaapps.menza.ui.util.nodeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

internal class RootNode(
    buildContext: BuildContext,
    private val spotlightModel: SpotlightModel<RootNavType> = SpotlightModel(
        RootNavType.types,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val spotlight: Spotlight<RootNavType> = Spotlight(
        model = spotlightModel,
        motionController = { SpotlightFader(it) },
    ),
    private val onDecided: () -> Unit,
) : ParentNode<RootNavType>(
    buildContext = buildContext,
    appyxComponent = spotlight,
) {
    override fun resolve(interactionTarget: RootNavType, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            LoadingNav -> node(buildContext) {} // Splash screen will be shown
            SetupFlowNav -> StartingNode(
                buildContext,
                {
                    lifecycleScope.launch {
                        spotlight.activateItem(spotlightModel, MainNav)
                    }
                },
            )

            MainNav -> MainNode(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: RootViewModel = nodeViewModel()

        HandleAppear(viewModel)
        val state by viewModel.flowState

        LaunchedEffect(state.isReady, state.isSetUp) {
            if (state.isReady) {
                (if (state.isSetUp) MainNav else SetupFlowNav)
                    .let { spotlight.activateItem(spotlightModel, it) }
            }
        }

        ApplyAppTheme(viewModel) {
            val activeIndex by remember { spotlightModel.activeIndex() }
                .collectAsStateWithLifecycle(-1)
            val indexOfType by remember { spotlightModel.indexOfType(LoadingNav) }
                .collectAsStateWithLifecycle(0)

            AppyxComponent(
                appyxComponent = spotlight,
                modifier = modifier.onPlaced {
                    if (indexOfType != activeIndex) {
                        onDecided()
                    }
                },
            )
        }
    }
}
