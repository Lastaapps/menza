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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.BackArrow
import cz.lastaapps.menza.ui.theme.Padding


@Composable
fun TwoPaneDialogLayout(
    showDetail: Boolean,
    onDismissDetail: () -> Unit,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    emptyNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    widthClass: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    when (widthClass) {
        WindowWidthSizeClass.Compact -> {
            TwoPaneDialogLayoutCompact(
                showDetail = showDetail,
                onDismissDetail = onDismissDetail,
                listNode = listNode,
                detailNode = detailNode,
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Medium -> {
            TwoPaneDialogLayoutMedium(
                showDetail = showDetail,
                onDismissDetail = onDismissDetail,
                listNode = listNode,
                detailNode = detailNode,
                emptyNode = emptyNode,
                modifier = modifier,
            )
        }

        WindowWidthSizeClass.Expanded -> {
            TwoPaneDialogLayoutExpanded(
                showDetail = showDetail,
                onDismissDetail = onDismissDetail,
                listNode = listNode,
                detailNode = detailNode,
                emptyNode = emptyNode,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun TwoPaneDialogLayoutCompact(
    showDetail: Boolean,
    onDismissDetail: () -> Unit,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        listNode()
    }

    if (showDetail) {
        Dialog(onDismissRequest = onDismissDetail) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                modifier = Modifier.fillMaxSize(.95f),
            ) {
                Box(modifier = Modifier.padding(Padding.Medium)) {
                    detailNode()
                }
            }
        }
    }
}

@Composable
private fun TwoPaneDialogLayoutMedium(
    showDetail: Boolean,
    onDismissDetail: () -> Unit,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    emptyNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) = TwoPaneDialogLayoutExpanded(
    showDetail = showDetail,
    onDismissDetail = onDismissDetail,
    listNode = listNode,
    detailNode = detailNode,
    emptyNode = emptyNode,
    modifier = modifier,
)

@Composable
private fun TwoPaneDialogLayoutExpanded(
    showDetail: Boolean,
    onDismissDetail: () -> Unit,
    listNode: @Composable () -> Unit,
    detailNode: @Composable () -> Unit,
    emptyNode: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BackArrow(showDetail) {
        onDismissDetail()
    }

    SplitLayout(
        panel1 = {
            Box(
                modifier = Modifier.padding(end = Padding.More.Screen / 2),
            ) {
                listNode()
            }
        },
        panel2 = {
            Crossfade(
                targetState = showDetail,
                modifier = Modifier.padding(start = Padding.More.Screen / 2),
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
