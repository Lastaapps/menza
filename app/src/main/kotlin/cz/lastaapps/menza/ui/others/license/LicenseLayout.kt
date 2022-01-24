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

package cz.lastaapps.menza.ui.others.license

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
import cz.lastaapps.menza.ui.root.AppLayoutMedium
import cz.lastaapps.osslicenseaccess.ArtifactLicense
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseLayout(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    licenseViewModel: LicenseViewModel,
) {
    val selected by licenseViewModel.selectedLicense.collectAsState()
    val onArtifactSelected: (ArtifactLicense?) -> Unit = {
        licenseViewModel.selectLicense(it)
    }

    @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            LicenseLayoutCompact(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                selectedArtifact = selected,
                onArtifactSelected = onArtifactSelected,
                licenseViewModel = licenseViewModel,
            )
        }
        WindowSizeClass.MEDIUM -> {
            LicenseLayoutMedium(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                selectedArtifact = selected,
                onArtifactSelected = onArtifactSelected,
                menzaViewModel = menzaViewModel,
                licenseViewModel = licenseViewModel,
            )
        }
        WindowSizeClass.EXPANDED -> {
            LicenseLayoutExpanded(
                navController = navController,
                snackbarHostState = snackbarHostState,
                drawerState = drawerState,
                expanded = expanded,
                onExpandedClicked = onExpandedClicked,
                menzaId = menzaId,
                onMenzaSelected = onMenzaSelected,
                menzaViewModel = menzaViewModel,
                selectedArtifact = selected,
                onArtifactSelected = onArtifactSelected,
                licenseViewModel = licenseViewModel,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseLayoutCompact(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    selectedArtifact: ArtifactLicense?,
    onArtifactSelected: (ArtifactLicense?) -> Unit,
    licenseViewModel: LicenseViewModel,
) {
    val scope = rememberCoroutineScope()

    AppLayoutCompact(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        enableIcon = true,
        showHamburgerMenu = selectedArtifact == null,
        onMenuButtonClicked = {
            if (selectedArtifact == null)
                scope.launch { drawerState.open() }
            else
                onArtifactSelected(null)
        },
    ) {
        BackHandler(enabled = selectedArtifact != null) {
            onArtifactSelected(null)
        }
        if (selectedArtifact == null) {
            LicenseList(
                licenseViewModel = licenseViewModel,
                onArtifactSelected = onArtifactSelected,
                Modifier.fillMaxSize()
            )
        } else {
            LicenseText(
                selectedArtifact = selectedArtifact,
                licenseViewModel = licenseViewModel,
                Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseLayoutMedium(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    selectedArtifact: ArtifactLicense?,
    onArtifactSelected: (ArtifactLicense?) -> Unit,
    licenseViewModel: LicenseViewModel,
) {
    AppLayoutMedium(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        showBackButton = selectedArtifact != null,
        onBackButtonPressed = {
            onArtifactSelected(null)
        },
    ) {
        BackHandler(enabled = selectedArtifact != null) {
            onArtifactSelected(null)
        }
        if (selectedArtifact == null) {
            LicenseList(
                licenseViewModel = licenseViewModel,
                onArtifactSelected = onArtifactSelected,
                Modifier.fillMaxSize()
            )
        } else {
            LicenseText(
                selectedArtifact = selectedArtifact,
                licenseViewModel = licenseViewModel,
                Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseLayoutExpanded(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    drawerState: DrawerState,
    expanded: Boolean,
    onExpandedClicked: () -> Unit,
    menzaId: MenzaId?,
    onMenzaSelected: (MenzaId?) -> Unit,
    menzaViewModel: MenzaViewModel,
    selectedArtifact: ArtifactLicense?,
    onArtifactSelected: (ArtifactLicense?) -> Unit,
    licenseViewModel: LicenseViewModel,
) {
    AppLayoutExpanded(
        navController = navController,
        menzaId = menzaId,
        onMenzaSelected = onMenzaSelected,
        menzaViewModel = menzaViewModel,
        snackbarHostState = snackbarHostState,
        drawerState = drawerState,
        expanded = expanded,
        onExpandedClicked = onExpandedClicked,
        showBackButton = false,
        panel1 = {
            LicenseList(
                licenseViewModel = licenseViewModel,
                onArtifactSelected = onArtifactSelected,
                Modifier.fillMaxSize()
            )
        },
        panel2 = {
            LicenseText(
                selectedArtifact = selectedArtifact,
                licenseViewModel = licenseViewModel,
                Modifier.fillMaxSize()
            )
        }
    )
}