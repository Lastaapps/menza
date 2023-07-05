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

package cz.lastaapps.menza.ui.util

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.MenzaPadding
import cz.lastaapps.menza.ui.theme.MenzaPadding.More


@Composable
fun PreviewWrapper(
    content: @Composable ColumnScope.() -> Unit,
) {
    AppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(More.Screen)
                    .width(300.dp),
                verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
                content = content,
            )
        }
    }
}
