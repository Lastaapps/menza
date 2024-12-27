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

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.today.ui.model.DishForRating
import cz.lastaapps.menza.features.today.ui.screen.RateDishScreen
import cz.lastaapps.menza.features.today.ui.vm.RateDishViewModel
import cz.lastaapps.menza.ui.components.BaseDialog
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

internal interface RateDishComponent {
    val viewModel: RateDishViewModel
    val dish: DishForRating

    fun dismiss()
}

internal class DefaultRateDishComponent(
    componentContext: ComponentContext,
    override val dish: DishForRating,
    private val onDismiss: () -> Unit,
) : RateDishComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: RateDishViewModel = getOrCreateKoin { parametersOf(dish) }

    override fun dismiss() {
        onDismiss()
    }
}

@Composable
internal fun RateDishContent(component: RateDishComponent) {
    BaseDialog(
        onDismissRequest = component::dismiss,
    ) {
        RateDishScreen(
            viewModel = component.viewModel,
            dish = component.dish,
            onSubmit = component::dismiss,
        )
    }
}
