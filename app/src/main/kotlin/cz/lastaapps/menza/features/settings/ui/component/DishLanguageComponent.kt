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

package cz.lastaapps.menza.features.settings.ui.component

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.settings.ui.screens.DishLanguageScreen
import cz.lastaapps.menza.features.settings.ui.vm.DishLanguageViewModel
import cz.lastaapps.menza.ui.theme.Padding.More
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface DishLanguageComponent {
    val viewModel: DishLanguageViewModel
}

internal class DefaultDishLanguageComponent(
    componentContext: ComponentContext,
) : DishLanguageComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: DishLanguageViewModel = getOrCreateKoin()
}

@Composable
internal fun DishLanguageContent(
    component: DishLanguageComponent,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DishLanguageScreen(
        onComplete = onNext,
        viewModel = component.viewModel,
        modifier =
            modifier
                .padding(More.Screen)
                .fillMaxSize(),
    )
}
