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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    menzaName: String?,
    menuIcon: ImageVector? = null,
    menuDescription: String? = null,
    menuRotated: Boolean = false,
    onMenuClicked: (() -> Unit)? = null,
) {

    val title = remember(menzaName) {
        menzaName ?: "No menza selected"
    }

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val scrollBehavior = remember(decayAnimationSpec) {
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(decayAnimationSpec)
    }

    SmallTopAppBar(
        title = { Text(title) },
        navigationIcon = {
            if (menuIcon != null) {
                IconButton(onClick = { onMenuClicked?.let { it() } }) {
                    val rotation by animateFloatAsState(if (!menuRotated) 0f else 90f)
                    Icon(
                        modifier = Modifier.rotate(rotation),
                        imageVector = menuIcon,
                        contentDescription = menuDescription
                    )
                }
            } else {
                Box(Modifier.size(48.dp))
            }
        },
        actions = {
            /*IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = {  }) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Localized description"
                )
            }*/
        },
        scrollBehavior = scrollBehavior
    )
}