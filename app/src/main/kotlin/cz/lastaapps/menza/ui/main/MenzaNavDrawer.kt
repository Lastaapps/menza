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

package cz.lastaapps.menza.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationDrawer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cz.lastaapps.entity.menza.MenzaId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaNavDrawer(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaListViewModel = hiltViewModel(),
    content: @Composable () -> Unit,
) {

    NavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            MenzaList(
                modifier = Modifier.padding(8.dp),
                selectedMenza = selectedMenza,
                onMenzaSelected = onMenzaSelected,
                menzaListViewModel = menzaListViewModel,
                expanded = true,
            )
        },
        content = content,
    )
}



