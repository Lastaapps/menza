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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.menza.R
import cz.lastaapps.menza.navigation.Dest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    navController: NavController,
    menzaName: String?,
    alignRail: Boolean,
    menuIcon: ImageVector? = null,
    menuDescription: String? = null,
    menuRotated: Boolean = false,
    onMenuClicked: (() -> Unit)? = null,
) {

    val title = remember(menzaName) { menzaName }
        ?: stringResource(R.string.ui_top_bar_no_menza)

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()

    var mainPopupExpanded by rememberSaveable { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                /*Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                    Surface(
                        Modifier.size(32.dp),
                        color = colorResource(R.color.ic_launcher_background),
                        contentColor = colorResource(R.color.ic_launcher_foreground),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Restaurant, null)
                        }
                    }
                }*/
                Text(title, textAlign = TextAlign.Center)
            }
        },
        navigationIcon = {
            val mod = if (alignRail) Modifier.width(76.dp) else Modifier //rail - 4.dp for padding
            Box(mod, contentAlignment = Alignment.Center) {
                if (menuIcon != null) {
                    IconButton(onClick = { onMenuClicked?.let { it() } }) {
                        val rotation by animateFloatAsState(if (!menuRotated) 0f else 90f)
                        Icon(
                            modifier = Modifier.rotate(rotation),
                            imageVector = menuIcon,
                            contentDescription = menuDescription
                        )
                    }
                }
            }
        },
        actions = {
            Box {
                IconButton({ mainPopupExpanded = !mainPopupExpanded }) {
                    Icon(
                        Icons.Default.MoreVert,
                        stringResource(R.string.ui_top_bar_action_description),
                    )
                }
                TopBarPopup(
                    mainPopupExpanded, { mainPopupExpanded = false },
                    Modifier.padding(top = 4.dp, start = 8.dp, bottom = 4.dp, end = 8.dp)
                ) {
                    navController.navigate(it) {
                        launchSingleTop = true
                    }
                }
            }
        },
    )
}

@Composable
private fun TopBarPopup(
    expanded: Boolean, onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    DropdownMenu(expanded, onDismissRequest, modifier) {
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_privacy)) },
            {
                navigateTo(Dest.R.privacyPolicy)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_license)) },
            {
                navigateTo(Dest.R.license)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_osturak)) },
            {
                navigateTo(Dest.R.osturak)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_web)) },
            {
                uriHandler.openUri("https://agata.suz.cvut.cz/jidelnicky/")
                onDismissRequest()
            })
    }
}
