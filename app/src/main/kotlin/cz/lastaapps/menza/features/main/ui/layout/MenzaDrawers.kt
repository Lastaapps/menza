/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex

@Composable
internal fun MenzaModalDrawer(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            if (!alternativeNavigation) {
                // handles predictive back gesture
                ModalDrawerSheet(drawerState) {
                    drawerContent()
                }
            } else {
                ModalDrawerSheet {
                    drawerContent()
                }
            }
        },
        content = content,
    )
}

@Composable
internal fun MenzaDismissibleDrawerWithRailLayout(
    rail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawer: @Composable () -> Unit,
) {
    Row(modifier) {
        Box(Modifier.zIndex(1f)) {
            rail()
        }
        drawer()
    }
}

@Composable
internal fun MenzaDismissibleDrawer(
    drawerState: DrawerState,
    alternativeNavigation: Boolean,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    DismissibleNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            if (!alternativeNavigation) {
                // handles predictive back gesture
                DismissibleDrawerSheet(drawerState) {
                    drawerContent()
                }
            } else {
                DismissibleDrawerSheet {
                    drawerContent()
                }
            }
        },
        content = content,
    )
}

@Suppress("unused")
@Composable
internal fun MenzaPermanentDrawer(
    @Suppress("UNUSED_PARAMETER")
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
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
