/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.main.ui.component

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
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.main.ui.screen.AgataLoginDialog
import cz.lastaapps.menza.features.main.ui.screen.MenzaSelectionScreen
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletLoginViewModel
import cz.lastaapps.menza.features.main.ui.vm.AgataWalletViewModel
import cz.lastaapps.menza.features.main.ui.vm.MenzaSelectionViewModel
import cz.lastaapps.menza.features.main.ui.widgets.AgataWalletButton
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

internal interface MenzaSelectionComponent {
    val agataWalletViewModel: AgataWalletViewModel
    val agataWalletLoginViewModel: AgataWalletLoginViewModel
    val selectionViewModel: MenzaSelectionViewModel
}

internal class DefaultMenzaSelectionComponent(
    componentContext: ComponentContext,
) : MenzaSelectionComponent, KoinComponent, ComponentContext by componentContext {
    override val agataWalletViewModel: AgataWalletViewModel = getOrCreateKoin()
    override val agataWalletLoginViewModel: AgataWalletLoginViewModel = getOrCreateKoin()
    override val selectionViewModel: MenzaSelectionViewModel = getOrCreateKoin()
}

@Composable
internal fun MenzaSelectionContent(
    component: MenzaSelectionComponent,
    onEdit: () -> Unit,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()

    var balanceLoginDialogShown by rememberSaveable { mutableStateOf(false) }

    val accountBalance: @Composable () -> Unit = {
        AgataWalletButton(
            viewModel = component.agataWalletViewModel,
            snackbarHostState = snackbarHostState,
            onShowLoginDialog = { balanceLoginDialogShown = true },
            modifier = Modifier.fillMaxWidth(),
        )
    }

    if (balanceLoginDialogShown) {
        AgataLoginDialog(viewModel = component.agataWalletLoginViewModel) {
            balanceLoginDialogShown = false
        }
    }

    MenzaSelectionScreen(
        onEdit = onEdit,
        onMenzaSelected = {
            scope.launch {
                when (drawerState.targetValue) {
                    Closed -> drawerState.open()
                    Open -> drawerState.close()
                }
            }
        },
        viewModel = component.selectionViewModel,
        accountBalance = accountBalance,
        modifier = modifier.fillMaxSize(),
    )
}
