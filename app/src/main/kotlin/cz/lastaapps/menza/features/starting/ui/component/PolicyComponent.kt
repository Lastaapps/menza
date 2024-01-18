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

package cz.lastaapps.menza.features.starting.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.other.ui.dialog.PrivacyDialogDest
import cz.lastaapps.menza.features.other.ui.vm.PolicyViewModel
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface PolicyComponent {
    val viewModel: PolicyViewModel

    val allowAccept: Boolean
}

internal class DefaultPolicyComponent(
    componentContext: ComponentContext,
    override val allowAccept: Boolean,
) : PolicyComponent, KoinComponent, ComponentContext by componentContext {
    override val viewModel: PolicyViewModel = getOrCreateKoin()
}

@Composable
internal fun PolicyContent(
    component: PolicyComponent,
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
) {
    Spacer(modifier = modifier)

    PrivacyDialogDest(
        onNotNeeded = onNext,
        viewModel = component.viewModel,
    )
}
