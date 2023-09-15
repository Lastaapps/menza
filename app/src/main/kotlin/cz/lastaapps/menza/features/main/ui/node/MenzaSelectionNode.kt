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

import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.DrawerValue.Open
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import cz.lastaapps.menza.features.main.ui.screen.MenzaSelectionScreen
import cz.lastaapps.menza.ui.util.nodeViewModel
import kotlinx.coroutines.launch

class MenzaSelectionNode(
    buildContext: BuildContext,
    private val onEdit: () -> Unit,
    private val updateDrawer: () -> DrawerState?,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val scope = rememberCoroutineScope()

        MenzaSelectionScreen(
            onEdit = onEdit,
            onMenzaSelected = {
                scope.launch {
                    val drawerState = updateDrawer() ?: return@launch
                    when (drawerState.targetValue) {
                        Closed -> Open
                        Open -> Closed
                    }.let { drawerState.animateTo(it, spring()) }
                }
            },
            viewModel = nodeViewModel(),
            modifier = modifier.fillMaxSize(),
        )
    }
}
