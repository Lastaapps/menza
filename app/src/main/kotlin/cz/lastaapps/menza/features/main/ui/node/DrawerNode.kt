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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import cz.lastaapps.menza.features.main.ui.node.DrawerNavType.EDIT_NAV
import cz.lastaapps.menza.features.main.ui.node.DrawerNavType.MENZA_LIST_NAV
import cz.lastaapps.menza.features.settings.ui.nodes.ReorderMenzaNode

internal enum class DrawerNavType {
    MENZA_LIST_NAV, EDIT_NAV,
    ;
}

internal class DrawerNode(
    buildContext: BuildContext,
    private val drawableStateProvider: () -> DrawerState?,
    private val backstack: BackStack<DrawerNavType> = BackStack(
        model = BackStackModel(
            initialTargets = listOf(MENZA_LIST_NAV),
            savedStateMap = buildContext.savedStateMap,
        ),
        motionController = { BackStackFader(it) },
    ),
) : ParentNode<DrawerNavType>(backstack, buildContext) {

    override fun resolve(interactionTarget: DrawerNavType, buildContext: BuildContext): Node =
        when (interactionTarget) {
            MENZA_LIST_NAV -> MenzaSelectionNode(
                buildContext,
                onEdit = { backstack.push(EDIT_NAV) },
                updateDrawer = drawableStateProvider,
            )

            EDIT_NAV -> ReorderMenzaNode(buildContext, backstack::pop)
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backstack,
            modifier = modifier,
        )
    }
}
