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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.MenzaPadding

@Composable
fun TwoPaneLayout(
    showDetail: Boolean,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    emptyNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    widthClass: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    when (widthClass) {
        WindowWidthSizeClass.Compact ->
            TwoPaneLayoutCompact(
                showDetail = showDetail,
                listNode = listNode,
                detailNode = detailNode,
                modifier = modifier,
            )

        WindowWidthSizeClass.Medium ->
            TwoPaneLayoutMedium(
                showDetail = showDetail,
                listNode = listNode,
                detailNode = detailNode,
                modifier = modifier,
            )

        WindowWidthSizeClass.Expanded ->
            TwoPaneLayoutExpanded(
                showDetail = showDetail,
                listNode = listNode,
                detailNode = detailNode,
                emptyNode = emptyNode,
                modifier = modifier,
            )
    }
}

@Composable
private fun TwoPaneLayoutCompact(
    showDetail: Boolean,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = showDetail,
        modifier = modifier,
    ) { show ->
        if (show) {
            detailNode()
        } else {
            listNode()
        }
    }
}

@Composable
private fun TwoPaneLayoutMedium(
    showDetail: Boolean,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) = TwoPaneLayoutCompact(
    showDetail = showDetail,
    listNode = listNode,
    detailNode = detailNode,
    modifier = modifier,
)

@Composable
private fun TwoPaneLayoutExpanded(
    showDetail: Boolean,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    emptyNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    SplitLayout(
        panel1 = {
            Box(
                modifier = Modifier.padding(end = MenzaPadding.More.Screen / 2),
            ) {
                listNode()
            }
        },
        panel2 = {
            Crossfade(
                targetState = showDetail,
                modifier = Modifier.padding(start = MenzaPadding.More.Screen / 2),
            ) { currentShowDetail ->
                if (currentShowDetail) {
                    detailNode()
                } else {
                    emptyNode()
                }
            }
        },
        modifier = modifier,
    )
}
