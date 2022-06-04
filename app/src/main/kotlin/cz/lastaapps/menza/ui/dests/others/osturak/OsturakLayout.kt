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

package cz.lastaapps.menza.ui.dests.others.osturak

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.menza.ui.root.BackArrow
import cz.lastaapps.menza.ui.root.UseSplitLayout
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.locals.WindowSizeClass

@Composable
fun OsturakLayout(
    navController: NavController,
) {
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            OsturakLayoutCompact(
                navController = navController,
            )
        }
        WindowSizeClass.MEDIUM -> {
            OsturakLayoutMedium(
                navController = navController,
            )
        }
        WindowSizeClass.EXPANDED -> {
            OsturakLayoutExpanded()
        }
    }
}

@Composable
fun OsturakLayoutCompact(navController: NavController) {
    BackArrow {
        navController.navigateUp()
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OsturakText()
            OsturakImages(Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun OsturakLayoutMedium(
    navController: NavController
) = OsturakLayoutCompact(
    navController = navController,
)

@Composable
fun OsturakLayoutExpanded() {
    UseSplitLayout(
        panel1 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) { OsturakText() }
        },
        panel2 = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                contentAlignment = Alignment.Center
            ) { OsturakImages() }
        })
}
