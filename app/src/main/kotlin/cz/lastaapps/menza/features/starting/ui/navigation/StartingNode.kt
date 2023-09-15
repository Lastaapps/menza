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

package cz.lastaapps.menza.features.starting.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.next
import com.bumble.appyx.components.spotlight.ui.slider.SpotlightSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import cz.lastaapps.menza.features.other.ui.dialog.PrivacyDialogDest
import cz.lastaapps.menza.features.settings.ui.nodes.AppThemeNode
import cz.lastaapps.menza.features.settings.ui.nodes.ReorderMenzaNode
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.AllSetNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.ChoosePriceNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.ChooseThemeNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.DownloadDataNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.OrderMenzaListNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNavType.PolicyBackgroundNav
import cz.lastaapps.menza.features.starting.ui.node.AllSetNode
import cz.lastaapps.menza.features.starting.ui.node.DownloadNode
import cz.lastaapps.menza.features.starting.ui.node.PriceTypeNode
import cz.lastaapps.menza.ui.util.activeIndex
import cz.lastaapps.menza.ui.util.nodeViewModel
import kotlinx.coroutines.flow.first

class StartingNode(
    buildContext: BuildContext,
    private val onDone: () -> Unit,
    private val spotlightModel: SpotlightModel<StartingNavType> = SpotlightModel(
        items = StartingNavType.allTypes,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val spotlight: Spotlight<StartingNavType> = Spotlight<StartingNavType>(
        model = spotlightModel,
        motionController = { SpotlightSlider(it) },
    ),
) : ParentNode<StartingNavType>(
    appyxComponent = spotlight,
    buildContext = buildContext,
) {

    override fun resolve(interactionTarget: StartingNavType, buildContext: BuildContext): Node {
        val onNext = { spotlight.next() }
        return when (interactionTarget) {
            PolicyBackgroundNav -> node(buildContext) {}
            DownloadDataNav -> DownloadNode(buildContext, onNext)
            ChooseThemeNav -> AppThemeNode(buildContext, onNext)
            ChoosePriceNav -> PriceTypeNode(buildContext, onNext)
            OrderMenzaListNav -> ReorderMenzaNode(buildContext, onNext)
            AllSetNav -> AllSetNode(buildContext, onDone)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Scaffold(
            modifier = modifier,
        ) {
            AppyxComponent(
                appyxComponent = spotlight,
                modifier = Modifier.padding(it),
            )

            PrivacyDialogDest(
                onNotNeeded = {
                    if (spotlightModel.activeIndex().first() == 0) {
                        spotlight.next()
                    }
                },
                viewModel = nodeViewModel(),
            )
        }
    }
}
