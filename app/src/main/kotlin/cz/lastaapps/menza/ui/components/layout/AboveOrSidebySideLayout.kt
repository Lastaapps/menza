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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
    topLeft: @Composable ColumnScope.() -> Unit,
    bottomRight: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
    verticalSpacer: @Composable ColumnScope.() -> Unit = {
        Spacer(Modifier.height(MenzaPadding.Small))
    },
) {
    when (LocalWindowWidth.current) {
        WindowWidthSizeClass.Compact -> {
            AboveOrSideBySideCompact(
                topLeft = topLeft,
                bottomRight = bottomRight,
                verticalSpacer = verticalSpacer,
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Medium -> {
            AboveOrSideBySideMedium(
                topLeft = topLeft,
                bottomRight = bottomRight,
                verticalSpacer = verticalSpacer,
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
    topLeft: @Composable ColumnScope.() -> Unit,
    bottomRight: @Composable ColumnScope.() -> Unit,
    verticalSpacer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Medium),
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        topLeft()
        verticalSpacer()
        bottomRight()
    }
}

@Composable
private fun AboveOrSideBySideMedium(
    topLeft: @Composable ColumnScope.() -> Unit,
    bottomRight: @Composable ColumnScope.() -> Unit,
    verticalSpacer: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) = AboveOrSideBySideCompact(
    topLeft = topLeft,
    bottomRight = bottomRight,
    verticalSpacer = verticalSpacer,
    modifier = modifier,
)

@Composable
private fun AboveOrSideBySideExpanded(
    topLeft: @Composable ColumnScope.() -> Unit,
    bottomRight: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    SplitLayout(
        modifier = modifier,
        panel1 = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { topLeft() }
        },
        panel2 = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) { bottomRight() }
        },
    )
}
