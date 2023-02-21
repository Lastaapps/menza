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

package cz.lastaapps.menza.features.week.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.main.ui.components.WrapMenzaNotSelected
import cz.lastaapps.menza.features.week.ui.components.WeekDishList
import cz.lastaapps.menza.features.week.ui.vm.WeekState
import cz.lastaapps.menza.features.week.ui.vm.WeekViewModel
import cz.lastaapps.menza.ui.HandleError
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun WeekScreen(
    onOsturak: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeekViewModel = koinViewModel(),
    hostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    WeekEffects(viewModel, hostState)

    val state by viewModel.flowState
    WeekContent(
        state = state,
        onRefresh = viewModel::reload,
        noItems = viewModel::openWebMenu,
        onOsturak = onOsturak,
        modifier = modifier,
        hostState = hostState,
    )
}

@Composable
private fun WeekEffects(
    viewModel: WeekViewModel,
    hostState: SnackbarHostState,
) {
    HandleAppear(viewModel)
    HandleError(viewModel, hostState)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeekContent(
    state: WeekState,
    onRefresh: () -> Unit,
    noItems: () -> Unit,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(hostState) },
        modifier = modifier,
    ) { padding ->
        WrapMenzaNotSelected(
            menza = state.selectedMenza,
            onOsturak = onOsturak,
        ) {
            Crossfade(targetState = state.items) { items ->
                WeekDishList(
                    data = items,
                    priceType = state.priceType,
                    isLoading = state.isLoading,
                    onRefresh = onRefresh,
                    noItems = noItems,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                )
            }
        }
    }
}
