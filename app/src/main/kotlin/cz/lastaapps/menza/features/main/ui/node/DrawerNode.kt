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

package cz.lastaapps.menza.features.main.ui.node

import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import cz.lastaapps.menza.features.main.ui.node.DrawerNavType.EDIT
import cz.lastaapps.menza.features.main.ui.node.DrawerNavType.MENZA_LIST
import cz.lastaapps.menza.features.settings.ui.nodes.ReorderMenzaNode

internal enum class DrawerNavType {
    MENZA_LIST, EDIT,
    ;
}

@OptIn(ExperimentalMaterial3Api::class)
internal class DrawerNode constructor(
    buildContext: BuildContext,
    private val drawableStateProvider: () -> DrawerState?,
    private val backstack: BackStack<DrawerNavType> = BackStack(
        initialElement = MENZA_LIST,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<DrawerNavType>(backstack, buildContext) {

    override fun resolve(navTarget: DrawerNavType, buildContext: BuildContext): Node =
        when (navTarget) {
            MENZA_LIST -> MenzaSelectionNode(
                buildContext,
                onEdit = { backstack.push(EDIT) },
                updateDrawer = drawableStateProvider,
            )

            EDIT -> ReorderMenzaNode(buildContext, backstack::pop)
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            modifier = modifier,
            navModel = backstack,
            transitionHandler = rememberBackstackFader(),
        )
    }
}
