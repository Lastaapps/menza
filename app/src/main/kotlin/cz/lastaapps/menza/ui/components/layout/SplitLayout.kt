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

package cz.lastaapps.menza.ui.components.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.lastaapps.menza.ui.root.locals.LocalSplitPosition

@Composable
fun SplitLayout(
    panel1: @Composable () -> Unit,
    panel2: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier) {
        val size = LocalSplitPosition.current
        Box(
            Modifier.width(size.first)
        ) { panel1() }

        Spacer(Modifier.width(size.second))

        Box(
            Modifier.width(size.third)
        ) { panel2() }
    }
}