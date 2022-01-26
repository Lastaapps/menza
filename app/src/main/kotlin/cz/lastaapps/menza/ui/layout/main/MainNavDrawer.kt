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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.common.DeveloperInfo
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaNavDrawer(
    selectedMenza: MenzaId?,
    onMenzaSelected: (MenzaId) -> Unit,
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    menzaListViewModel: MenzaViewModel,
    content: @Composable () -> Unit,
) {

    NavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        drawerContent = {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "ČVUT Menza", style = MaterialTheme.typography.headlineMedium
                )

                MenzaList(
                    selectedMenza = selectedMenza,
                    onMenzaSelected = onMenzaSelected,
                    menzaListViewModel = menzaListViewModel,
                    expanded = true,
                    modifier = Modifier.weight(1f),
                )

                Text(
                    text = DeveloperInfo.getNameAndBuildYear(LocalContext.current),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                )
            }
        },
        content = content,
    )
}



