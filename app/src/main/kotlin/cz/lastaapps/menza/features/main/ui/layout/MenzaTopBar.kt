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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.Menza
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.main.ui.layout.TopBarNavTarget.LicenseNotices
import cz.lastaapps.menza.features.main.ui.layout.TopBarNavTarget.Osturak
import cz.lastaapps.menza.features.main.ui.layout.TopBarNavTarget.PrivacyPolicy
import cz.lastaapps.menza.ui.components.AppIcon
import cz.lastaapps.menza.ui.locals.LocalMenuBackArrow
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.MenuBackArrow
import cz.lastaapps.menza.ui.theme.MenzaPadding
import kotlinx.coroutines.launch


data class TopBarState(
    val alignRail: Boolean,
    val enableHamburger: Boolean,
    val enableRotation: Boolean = false, // used for an animation while using modal Drawer
)

@Suppress("SameParameterValue")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenzaTopBar(
    state: TopBarState,
    drawerState: DrawerState,
    menza: Menza?,
    onAction: (TopBarNavTarget) -> Unit,
    modifier: Modifier = Modifier,
    menuBack: MenuBackArrow = LocalMenuBackArrow.current,
) {
    val scope = rememberCoroutineScope()
    val onBackArrowClick = menuBack::runLast

    val onHamburgerClick: () -> Unit = {
        scope.launch {
            if (drawerState.targetValue == DrawerValue.Open) {
                drawerState.close()
            } else {
                drawerState.open()
            }
        }
    }

    val title = menza?.name ?: stringResource(R.string.ui_top_bar_no_menza)

    TopBarScaffold(
        title = title,
        alignRail = state.alignRail,
        leadingIcon = {
            LeadingIcon(
                state = state,
                drawerState = drawerState,
                onBackClick = onBackArrowClick,
                onHamburgerClick = onHamburgerClick,
                menuBack = menuBack,
            )
        },
        onAction = onAction,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBarScaffold(
    title: String,
    alignRail: Boolean,
    leadingIcon: @Composable () -> Unit,
    onAction: (TopBarNavTarget) -> Unit,
    modifier: Modifier = Modifier,
    windowWidth: WindowWidthSizeClass = LocalWindowWidth.current,
) {
    var mainPopupExpanded by rememberSaveable { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    MenzaPadding.Medium,
                    Alignment.CenterHorizontally,
                ),
            ) {
                if (windowWidth != WindowWidthSizeClass.Compact) {
                    AppIcon(size = 48.dp)
                }
                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        },
        navigationIcon = {
            val mod = if (alignRail) {
                Modifier.width(76.dp)
            } else {
                Modifier
            } //rail - 4.dp for padding
            Box(
                modifier = mod,
                contentAlignment = Alignment.Center,
            ) {
                leadingIcon()
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
                    mainPopupExpanded,
                    { mainPopupExpanded = false },
                    Modifier.padding(top = 4.dp, start = 8.dp, bottom = 4.dp, end = 8.dp),
                    navigateTo = onAction,
                )
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LeadingIcon(
    state: TopBarState,
    drawerState: DrawerState,
    onBackClick: () -> Unit,
    onHamburgerClick: () -> Unit,
    menuBack: MenuBackArrow,
    modifier: Modifier = Modifier,
) {
    val showBackArrow = menuBack.shouldShowBackArrow()

    val isRotated = remember(drawerState.targetValue) {
        drawerState.targetValue == DrawerValue.Open && state.enableRotation
    }
    val rotation by animateFloatAsState(if (!isRotated) 0f else 90f)

    val leadingIcon = when {
        showBackArrow -> Icons.Default.ArrowBack
        state.enableHamburger -> Icons.Default.Menu
        else -> null
    }
    val leadingContentDescription = when {
        showBackArrow -> stringResource(R.string.ui_top_bar_back_arrow)
        state.enableHamburger -> stringResource(R.string.ui_top_bar_show_menza_list)
        else -> null
    }

    Crossfade(
        targetState = leadingIcon,
        modifier = modifier,
    ) { icon ->
        icon?.let {
            IconButton(
                onClick = {
                    when {
                        showBackArrow -> onBackClick()
                        state.enableHamburger -> onHamburgerClick()
                    }
                },
                modifier = Modifier.rotate(rotation),
            ) {
                Icon(icon, leadingContentDescription)
            }
        }
    }
}


@Composable
private fun TopBarPopup(
    expanded: Boolean, onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    navigateTo: (TopBarNavTarget) -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    DropdownMenu(expanded, onDismissRequest, modifier) {
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_privacy)) },
            {
                navigateTo(PrivacyPolicy)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_license)) },
            {
                navigateTo(LicenseNotices)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_osturak)) },
            {
                navigateTo(Osturak)
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_web_agata)) },
            {
                uriHandler.openUri("https://agata.suz.cvut.cz/jidelnicky/")
                onDismissRequest()
            })
        DropdownMenuItem(
            { Text(stringResource(R.string.ui_top_bar_action_web_buffet)) },
            {
                uriHandler.openUri("http://studentcatering.cz/jidelni-listek/")
                onDismissRequest()
            })
    }
}

enum class TopBarNavTarget {
    PrivacyPolicy, LicenseNotices, Osturak,
}
