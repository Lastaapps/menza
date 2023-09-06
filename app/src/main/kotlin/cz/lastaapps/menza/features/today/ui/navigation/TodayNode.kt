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

package cz.lastaapps.menza.features.today.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import cz.lastaapps.menza.features.panels.Panels
import cz.lastaapps.menza.features.today.ui.screen.TodayScreen
import cz.lastaapps.menza.ui.theme.MenzaPadding

class TodayNode(
    buildContext: BuildContext,
    private val onOsturak: () -> Unit,
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        val panels: @Composable (Modifier) -> Unit = {
            Panels(modifier = it)
        }

        TodayScreen(
            onOsturak = onOsturak,
            panels = panels,
            modifier = Modifier
                .padding(MenzaPadding.More.Screen)
                .fillMaxSize(),
        )
    }
}
