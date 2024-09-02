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

package cz.lastaapps.menza.features.today.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.panels.Panels
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel
import cz.lastaapps.menza.features.panels.rateus.ui.RateUsViewModel
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel
import cz.lastaapps.menza.features.today.ui.screen.TodayScreen
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.features.today.ui.vm.TodayViewModel
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface TodayComponent {
    val viewModel: TodayViewModel
    val dishListViewModel: DishListViewModel
    val crashesViewModel: CrashesViewModel
    val whatsNewViewModel: WhatsNewViewModel
    val rageUsViewModel: RateUsViewModel
}

internal class DefaultTodayComponent(
    componentContext: ComponentContext,
) : TodayComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: TodayViewModel = getOrCreateKoin()
    override val dishListViewModel: DishListViewModel = getOrCreateKoin()
    override val crashesViewModel: CrashesViewModel = getOrCreateKoin()
    override val whatsNewViewModel: WhatsNewViewModel = getOrCreateKoin()
    override val rageUsViewModel: RateUsViewModel = getOrCreateKoin()
}

@Composable
internal fun TodayContent(
    component: TodayComponent,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val panels: @Composable (Modifier) -> Unit = {
        Panels(
            modifier = it,
            hostState = hostState,
            crashesViewModel = component.crashesViewModel,
            whatsNewViewModel = component.whatsNewViewModel,
            rateUsViewModel = component.rageUsViewModel,
        )
    }

    TodayScreen(
        onOsturak = onOsturak,
        panels = panels,
        viewModel = component.viewModel,
        dishListViewModel = component.dishListViewModel,
        hostState = hostState,
        modifier =
            modifier
                .padding(Padding.More.Screen)
                .fillMaxSize(),
    )
}
