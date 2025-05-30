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

package cz.lastaapps.menza.ui.components.layout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding

@Composable
fun AboveOrSideBySideLayout(
    topLeft: LazyListScope.() -> Unit,
    bottomRight: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
    verticalSpacer: LazyListScope.() -> Unit = {
        item { Spacer(Modifier.height(Padding.Small)) }
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
    topLeft: LazyListScope.() -> Unit,
    bottomRight: LazyListScope.() -> Unit,
    verticalSpacer: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth(),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Padding.Medium),
            modifier = Modifier.sizeIn(maxWidth = Padding.More.MaxColumnWidth),
        ) {
            topLeft()
            verticalSpacer()
            bottomRight()
            item {
                Spacer(Modifier.height(Padding.More.ScrollBottomSpace))
            }
        }
    }
}

@Composable
private fun AboveOrSideBySideMedium(
    topLeft: LazyListScope.() -> Unit,
    bottomRight: LazyListScope.() -> Unit,
    verticalSpacer: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
) = AboveOrSideBySideCompact(
    topLeft = topLeft,
    bottomRight = bottomRight,
    verticalSpacer = verticalSpacer,
    modifier = modifier,
)

@Composable
private fun AboveOrSideBySideExpanded(
    topLeft: LazyListScope.() -> Unit,
    bottomRight: LazyListScope.() -> Unit,
    modifier: Modifier = Modifier,
) {
    SplitLayout(
        modifier = modifier,
        panel1 = {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(
                    modifier = Modifier.sizeIn(maxWidth = Padding.More.MaxColumnWidth),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    topLeft()
                    item { Spacer(Modifier.height(Padding.More.ScrollBottomSpace)) }
                }
            }
        },
        panel2 = {
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier.fillMaxWidth(),
            ) {
                LazyColumn(
                    modifier = Modifier.sizeIn(maxWidth = Padding.More.MaxColumnWidth),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    bottomRight()
                    item { Spacer(Modifier.height(Padding.More.ScrollBottomSpace)) }
                }
            }
        },
    )
}
