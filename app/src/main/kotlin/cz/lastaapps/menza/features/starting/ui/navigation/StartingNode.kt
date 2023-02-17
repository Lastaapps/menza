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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.backpresshandlerstrategies.DontHandleBackPress
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightSlider
import cz.lastaapps.menza.features.settings.ui.nodes.ChooseAppThemeNode
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
import cz.lastaapps.menza.features.starting.ui.screen.PrivacyDialogDest
import kotlinx.coroutines.flow.first

class StartingNode(
    buildContext: BuildContext,
    private val onDone: () -> Unit,
    private val spotlight: Spotlight<StartingNavType> = Spotlight<StartingNavType>(
        items = StartingNavType.allTypes,
        savedStateMap = buildContext.savedStateMap,
        backPressHandler = DontHandleBackPress(),
    ),
) : ParentNode<StartingNavType>(
    buildContext = buildContext,
    navModel = spotlight,
) {

    override fun resolve(navTarget: StartingNavType, buildContext: BuildContext): Node {
        val onNext = { spotlight.next() }
        return when (navTarget) {
            PolicyBackgroundNav -> node(buildContext) {}
            DownloadDataNav -> DownloadNode(buildContext, onNext)
            ChooseThemeNav -> ChooseAppThemeNode(buildContext, onNext)
            ChoosePriceNav -> PriceTypeNode(buildContext, onNext)
            OrderMenzaListNav -> ReorderMenzaNode(buildContext, onNext)
            AllSetNav -> AllSetNode(buildContext, onDone)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun View(modifier: Modifier) {
        Scaffold(
            modifier = modifier,
        ) {
            Children(
                modifier = Modifier.padding(it),
                navModel = spotlight,
                transitionHandler = rememberSpotlightSlider(),
            )

            PrivacyDialogDest(onNotNeeded = {
                if (spotlight.activeIndex().first() == 0) {
                    spotlight.next()
                }
            })
        }
    }
}
