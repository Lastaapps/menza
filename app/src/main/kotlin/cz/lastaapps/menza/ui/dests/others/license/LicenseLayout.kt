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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library
import com.mikepenz.aboutlibraries.util.withContext
import cz.lastaapps.menza.ui.root.UseSplitLayout
import cz.lastaapps.menza.ui.root.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.root.locals.WindowSizeClass

@Composable
fun LicenseLayout() {
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

    when (LocalWindowWidth.current) {
        WindowSizeClass.COMPACT -> {
            LicenseLayoutCompact(
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
            )
        }
        WindowSizeClass.MEDIUM -> {
            LicenseLayoutMedium(
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
            )
        }
        WindowSizeClass.EXPANDED -> {
            LicenseLayoutExpanded(
                libraries = libraryList,
                selectedLibrary = selectedLibrary,
                onLibrarySelected = onLibrarySelected,
            )
        }
    }
}

@Composable
fun LicenseLayoutCompact(
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
) {
//    BackArrow(selectedLibrary != null) {
//        onLibrarySelected(null)
//    }

    LibraryList(libraries, onLibrarySelected = onLibrarySelected, Modifier.fillMaxSize())

    if (selectedLibrary != null) {
        Dialog(onDismissRequest = { onLibrarySelected(null) }) {
            Surface(shape = MaterialTheme.shapes.extraLarge, modifier = Modifier.fillMaxSize(.9f)) {
                LibraryDetail(library = selectedLibrary, Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun LicenseLayoutMedium(
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
) = LicenseLayoutExpanded(
    libraries = libraries,
    selectedLibrary = selectedLibrary,
    onLibrarySelected = onLibrarySelected,
)

@Composable
fun LicenseLayoutExpanded(
    libraries: List<Library>,
    selectedLibrary: Library?,
    onLibrarySelected: (Library?) -> Unit,
) {
    UseSplitLayout(
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