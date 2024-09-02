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

package cz.lastaapps.menza.features.other.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.lastaapps.menza.features.other.ui.widgets.OsturakImages
import cz.lastaapps.menza.features.other.ui.widgets.OsturakText
import cz.lastaapps.menza.ui.components.layout.AboveOrSideBySideLayout

@Composable
internal fun OsturakScreen(modifier: Modifier = Modifier) {
    OsturakContent(
        modifier = modifier,
    )
}

@Composable
private fun OsturakContent(modifier: Modifier = Modifier) {
    AboveOrSideBySideLayout(
        topLeft = {
            item {
                OsturakText()
            }
        },
        bottomRight = {
            item {
                OsturakImages()
            }
        },
        modifier = modifier,
    )
}
