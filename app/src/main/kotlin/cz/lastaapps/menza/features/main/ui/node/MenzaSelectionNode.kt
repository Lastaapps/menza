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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.DrawerValue.Open
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import cz.lastaapps.menza.features.main.ui.components.AgataWalletButton
import cz.lastaapps.menza.features.main.ui.screen.AgataLoginDialog
import cz.lastaapps.menza.features.main.ui.screen.MenzaSelectionScreen
import cz.lastaapps.menza.ui.util.nodeViewModel
import kotlinx.coroutines.launch

class MenzaSelectionNode(
    buildContext: BuildContext,
    private val onEdit: () -> Unit,
    private val updateDrawer: () -> DrawerState?,
    private val snackbarHostState: SnackbarHostState,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val scope = rememberCoroutineScope()

        var balanceLoginDialogShown by rememberSaveable { mutableStateOf(false) }

        val accountBalance: @Composable () -> Unit = {
            AgataWalletButton(
                viewModel = nodeViewModel(),
                snackbarHostState = snackbarHostState,
                onShowLoginDialog = { balanceLoginDialogShown = true },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (balanceLoginDialogShown) {
            AgataLoginDialog(viewModel = nodeViewModel()) {
                balanceLoginDialogShown = false
            }
        }

        MenzaSelectionScreen(
            onEdit = onEdit,
            onMenzaSelected = {
                scope.launch {
                    val drawerState = updateDrawer() ?: return@launch
                    when (drawerState.targetValue) {
                        Closed -> drawerState.open()
                        Open -> drawerState.close()
                    }
                }
            },
            viewModel = nodeViewModel(),
            accountBalance = accountBalance,
            modifier = modifier.fillMaxSize(),
        )
    }
}
