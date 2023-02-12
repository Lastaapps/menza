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

package cz.lastaapps.menza.features.today.ui.navigation

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import cz.lastaapps.menza.features.main.ui.layout.SplitLayout
import cz.lastaapps.menza.features.today.ui.navigation.TodayNavType.DishDetail
import cz.lastaapps.menza.features.today.ui.navigation.TodayNavType.DishList
import cz.lastaapps.menza.features.today.ui.navigation.TodayNavType.NoDishSelected
import cz.lastaapps.menza.features.today.ui.node.DishDetailNode
import cz.lastaapps.menza.features.today.ui.node.DishListNode
import cz.lastaapps.menza.features.today.ui.node.NoDishSelectedNode
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth

class TodayNode(
    buildContext: BuildContext,
    private val onOsturak: () -> Unit,
    private val backStack: BackStack<TodayNavType> = BackStack<TodayNavType>(
        initialElement = TodayNavType.DishList,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<TodayNavType>(backStack, buildContext) {
    override fun resolve(navTarget: TodayNavType, buildContext: BuildContext): Node =
        when (navTarget) {
            DishList -> DishListNode(
                buildContext,
                onDetail = {
                    backStack.push(TodayNavType.DishDetail(0))
                },
                onOsturak = onOsturak,
            )

            is DishDetail -> DishDetailNode(buildContext)
            NoDishSelected -> NoDishSelectedNode(buildContext)
        }

    @Composable
    override fun View(modifier: Modifier) {
        when (LocalWindowWidth.current) {
            WindowWidthSizeClass.Compact,
            WindowWidthSizeClass.Medium,
            -> {
                Children(
                    navModel = backStack,
                    transitionHandler = rememberBackstackFader(),
                )
            }

            WindowWidthSizeClass.Expanded,
            -> {
                SplitLayout(
                    panel1 = {
                        PermanentChild(navTarget = DishList)
                    },
                    panel2 = {
                        val elements by backStack.elements.collectAsState()

                        if (elements.size <= 1) {
                            PermanentChild(navTarget = NoDishSelected)
                        } else {
                            Children(navModel = backStack)
                        }
                    }
                )
            }
        }
    }
}
