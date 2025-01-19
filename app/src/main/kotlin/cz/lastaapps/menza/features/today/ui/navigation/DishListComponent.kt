/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.menza.features.panels.Panels
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel
import cz.lastaapps.menza.features.panels.rateus.ui.RateUsViewModel
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel
import cz.lastaapps.menza.features.today.ui.screen.DishListScreen
import cz.lastaapps.menza.features.today.ui.vm.DishListViewModel
import cz.lastaapps.menza.ui.util.AnimationScopes
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface DishListComponent {
    val dishListViewModel: DishListViewModel
    val crashesViewModel: CrashesViewModel
    val whatsNewViewModel: WhatsNewViewModel
    val rageUsViewModel: RateUsViewModel

    val onDishSelected: (Dish) -> Unit
}

internal class DefaultDishListComponent(
    componentContext: ComponentContext,
    override val onDishSelected: (Dish) -> Unit,
) : DishListComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val dishListViewModel: DishListViewModel = getOrCreateKoin()
    override val crashesViewModel: CrashesViewModel = getOrCreateKoin()
    override val whatsNewViewModel: WhatsNewViewModel = getOrCreateKoin()
    override val rageUsViewModel: RateUsViewModel = getOrCreateKoin()
}

@Composable
internal fun DishListContent(
    component: DishListComponent,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    scopes: AnimationScopes,
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

    DishListScreen(
        viewModel = component.dishListViewModel,
        panels = panels,
        onOsturak = onOsturak,
        onDish = component.onDishSelected,
        onRating = { error("Not supported") },
        hostState = hostState,
        scopes = scopes,
        modifier = modifier,
    )
}
