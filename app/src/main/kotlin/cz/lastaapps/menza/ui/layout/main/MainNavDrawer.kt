/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.layout.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.layout.menza.MenzaList
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaModalDrawer(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaViewModel,
    lazyListState: LazyListState = LocalDrawerListState.current,
    content: @Composable () -> Unit,
) {
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                DrawerContent(selectedMenza, onMenzaSelected, menzaListViewModel, lazyListState)
            }
        },
        content = content,
    )
}

@Composable
fun MenzaDismissibleDrawerWithRailLayout(
    modifier: Modifier = Modifier,
    rail: @Composable () -> Unit,
    drawer: @Composable () -> Unit,
) {
    Box(modifier) {
        Row {
            Spacer(modifier = Modifier.width(80.dp)) // rail width
            drawer()
        }
        rail()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaDismissibleDrawer(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaViewModel,
    lazyListState: LazyListState = LocalDrawerListState.current,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    DismissibleNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            DismissibleDrawerSheet {
                DrawerContent(selectedMenza, {
                    scope.launch { drawerState.close() }
                    onMenzaSelected(it)
                }, menzaListViewModel, lazyListState)
            }
        },
        content = content,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaPermanentDrawer(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    @Suppress("UNUSED_PARAMETER")
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaViewModel,
    lazyListState: LazyListState = LocalDrawerListState.current,
    content: @Composable () -> Unit,
) {
    PermanentNavigationDrawer(
        modifier = modifier,
        drawerContent = {
            PermanentDrawerSheet {
                DrawerContent(selectedMenza, onMenzaSelected, menzaListViewModel, lazyListState)
            }
        },
        content = content,
    )
}

@Composable
private fun DrawerContent(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    menzaListViewModel: MenzaViewModel,
    lazyListState: LazyListState,
) {
    /*Text(
        stringResource(R.string.app_name_long),
        style = MaterialTheme.typography.headlineMedium
    )*/

    MenzaList(
        selectedMenza = selectedMenza,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaListViewModel,
        lazyListState = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp, end = 16.dp),
    )
}

