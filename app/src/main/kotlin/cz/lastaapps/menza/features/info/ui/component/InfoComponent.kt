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

package cz.lastaapps.menza.features.info.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.info.ui.screen.InfoScreen
import cz.lastaapps.menza.features.info.ui.vm.InfoViewModel
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface InfoComponent {
    val viewModel: InfoViewModel
}

internal class DefaultInfoComponent(
    componentContext: ComponentContext,
) : InfoComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: InfoViewModel = getOrCreateKoin()
}

@Composable
internal fun InfoContent(
    component: InfoComponent,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    InfoScreen(
        viewModel = component.viewModel,
        onOsturak = onOsturak,
        hostState = hostState,
        modifier = modifier.fillMaxWidth(),
    )
}
