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

package cz.lastaapps.menza.ui.dests.others.license

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel
import cz.lastaapps.menza.ui.root.AppLayoutCompact
import cz.lastaapps.menza.ui.root.AppLayoutExpanded
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
) {
    val context = LocalContext.current
    var libraries by remember {
        mutableStateOf<Libs?>(null)
    }
    LaunchedEffect(libraries) {
        libraries = Libs.Builder().withContext(context).build()
    }

    var selectedArtifactUniqueId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedLibrary = remember(libraries, selectedArtifactUniqueId) {
        libraries?.libraries?.firstOrNull { it.uniqueId == selectedArtifactUniqueId }
    }
    val onLibrarySelected: (Library?) -> Unit = { selectedArtifactUniqueId = it?.uniqueId }

    if (libraries == null)
        return

    val libraryList = remember(libraries) {
        // to filter out wrongly named libraries
        libraries!!.libraries.filter { !it.name.startsWith("$") }
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
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
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
                menzaViewModel = menzaViewModel,
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
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
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
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
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
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
        showHamburgerMenu = selectedLibrary == null,
        onMenuButtonClicked = {
            if (selectedLibrary == null)
                scope.launch { drawerState.open() }
            else
                onLibrarySelected(null)
        },
    ) {
        BackHandler(enabled = selectedLibrary != null) {
            onLibrarySelected(null)
        }
        LibraryList(libraries, onLibrarySelected = onLibrarySelected, Modifier.fillMaxSize())

        if (selectedLibrary != null) {
            Dialog(onDismissRequest = { onLibrarySelected(null) }) {
                Surface(shape = RoundedCornerShape(16.dp)) {
                    LibraryDetail(library = selectedLibrary, Modifier.padding(16.dp))
                }
            }
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
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
) = LicenseLayoutExpanded(
    navController = navController,
    snackbarHostState = snackbarHostState,
    drawerState = drawerState,
    expanded = expanded,
    onExpandedClicked = onExpandedClicked,
    menzaId = menzaId,
    onMenzaSelected = onMenzaSelected,
    menzaViewModel = menzaViewModel,
    libraries = libraries,
    selectedLibrary = selectedLibrary,
    onLibrarySelected = onLibrarySelected,
)

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
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
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
            LibraryList(
                libraries, onLibrarySelected,
                Modifier.fillMaxSize()
            )
        },
        panel2 = {
            LibraryDetailWrapper(
                selectedLibrary,
                Modifier.fillMaxSize()
            )
        }
    )
}