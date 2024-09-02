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

package cz.lastaapps.menza.features.other.ui.node

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.other.ui.screen.LicenseScreen
import cz.lastaapps.menza.features.other.ui.vm.LicenseViewModel
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface LicenseComponent {
    val viewModel: LicenseViewModel
}

internal class DefaultLicenseComponent(
    componentContext: ComponentContext,
) : LicenseComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: LicenseViewModel = getOrCreateKoin()
}

@Composable
internal fun LicenseContent(
    component: LicenseComponent,
    modifier: Modifier = Modifier,
) {
    LicenseScreen(
        viewModel = component.viewModel,
        modifier =
            modifier
                .padding(Padding.More.Screen)
                .fillMaxSize(),
    )
}
