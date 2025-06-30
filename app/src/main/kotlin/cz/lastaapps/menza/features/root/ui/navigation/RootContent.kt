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

package cz.lastaapps.menza.features.root.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import cz.lastaapps.menza.features.main.ui.navigation.MainContent
import cz.lastaapps.menza.features.root.ui.RootViewModel
import cz.lastaapps.menza.features.root.ui.navigation.RootComponent.Child.AppContent
import cz.lastaapps.menza.features.root.ui.navigation.RootComponent.Child.AppSetup
import cz.lastaapps.menza.features.starting.ui.navigation.StartingContent

@Composable
internal fun RootContent(
    component: RootComponent,
    onReady: () -> Unit,
    modifier: Modifier = Modifier,
) = Surface {
    val viewModel: RootViewModel = component.viewModel
    val state by viewModel.flowState

    LaunchedEffect(state.isReady, state.isSetUp) {
        if (state.isReady) {
            if (state.isSetUp) {
                component.toAppContent()
            } else {
                component.toInitialSetup()
            }
        }
    }

    val slot by component.content.subscribeAsState()
    AnimatedContent(
        targetState = slot.child?.instance,
        label = "Root slot",
    ) { instance ->
        when (instance) {
            is AppContent -> MainContent(component = instance.component, modifier)

            is AppSetup ->
                StartingContent(
                    instance.component,
                    component::toAppContent,
                    modifier,
                )

            null -> Surface(modifier) {}
        }
        if (instance != null) {
            SideEffect {
                onReady()
            }
        }
    }
}
