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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.MenzaPadding

@Composable
fun AboveOrSideBySideLayout(
    topLeft: @Composable () -> Unit,
    bottomRight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (LocalWindowWidth.current) {
        WindowWidthSizeClass.Compact -> {
            AboveOrSideBySideCompact(
                topLeft = topLeft,
                bottomRight = bottomRight,
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Medium -> {
            AboveOrSideBySideMedium(
                topLeft = topLeft,
                bottomRight = bottomRight,
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Expanded -> {
            AboveOrSideBySideExpanded(
                topLeft = topLeft,
                bottomRight = bottomRight,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun AboveOrSideBySideCompact(
    topLeft: @Composable () -> Unit,
    bottomRight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Medium),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        topLeft()
        bottomRight()
    }
}

@Composable
private fun AboveOrSideBySideMedium(
    topLeft: @Composable () -> Unit,
    bottomRight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) = AboveOrSideBySideCompact(
    topLeft = topLeft,
    bottomRight = bottomRight,
    modifier = modifier,
)

@Composable
private fun AboveOrSideBySideExpanded(
    topLeft: @Composable () -> Unit,
    bottomRight: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    SplitLayout(
        modifier = modifier,
        panel1 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) { topLeft() }
        },
        panel2 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) { bottomRight() }
        },
    )
}
