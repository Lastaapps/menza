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

package cz.lastaapps.menza.starting.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.operation.next
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightSlider
import cz.lastaapps.menza.starting.ui.StartingNavType.ChoosePrice
import cz.lastaapps.menza.starting.ui.StartingNavType.ChooseTheme
import cz.lastaapps.menza.starting.ui.StartingNavType.DownloadData
import cz.lastaapps.menza.starting.ui.chooseprice.ChoosePriceNode
import cz.lastaapps.menza.starting.ui.choosetheme.ChooseThemeNode
import cz.lastaapps.menza.starting.ui.downloaddata.DownloadDataNode
import cz.lastaapps.menza.starting.ui.privacy.PrivacyDialogDest

class StartingNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<StartingNavType> = Spotlight<StartingNavType>(
        items = StartingNavType.allTypes,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<StartingNavType>(
    buildContext = buildContext,
    navModel = spotlight,
) {
    override fun resolve(navTarget: StartingNavType, buildContext: BuildContext): Node {
        val onNext = { spotlight.next() }
        return when (navTarget) {
            DownloadData -> DownloadDataNode(buildContext, onNext)
            ChoosePrice -> ChoosePriceNode(buildContext)
            ChooseTheme -> ChooseThemeNode(buildContext)
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

            PrivacyDialogDest()
        }
    }
}
