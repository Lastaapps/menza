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

package cz.lastaapps.menza.features.main.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import cz.lastaapps.menza.ui.theme.MenzaPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MenzaModalDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(MenzaPadding.MidSmall))
                drawerContent()
            }
        },
        content = content,
    )
}

@Composable
internal fun MenzaDismissibleDrawerWithRailLayout(
    modifier: Modifier = Modifier,
    rail: @Composable () -> Unit,
    drawer: @Composable () -> Unit,
) {
    Row(modifier) {
        Box(Modifier.zIndex(1f)) {
            rail()
        }
        drawer()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MenzaDismissibleDrawer(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    DismissibleNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DismissibleDrawerSheet {
                drawerContent()
            }
        },
        content = content,
    )
}

@Suppress("unused")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MenzaPermanentDrawer(
    @Suppress("UNUSED_PARAMETER")
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    drawerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            PermanentDrawerSheet {
                drawerContent()
            }
        },
        content = content,
    )
}
