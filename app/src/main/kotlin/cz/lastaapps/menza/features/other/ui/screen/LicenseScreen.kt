/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.other.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.entity.Library
import cz.lastaapps.menza.features.other.ui.vm.LicenseState
import cz.lastaapps.menza.features.other.ui.vm.LicenseViewModel
import cz.lastaapps.menza.features.other.ui.widgets.LibraryDetail
import cz.lastaapps.menza.features.other.ui.widgets.LibraryList
import cz.lastaapps.menza.features.other.ui.widgets.NoLibrarySelected
import cz.lastaapps.menza.ui.components.layout.TwoPaneDialogLayout

@Composable
internal fun LicenseScreen(
    viewModel: LicenseViewModel,
    modifier: Modifier = Modifier,
) {
    LicenseEffects(viewModel)

    val state by viewModel.flowState
    LicenseContent(
        state = state,
        onSelectLibrary = viewModel::selectLibrary,
        modifier = modifier,
    )
}

@Composable
private fun LicenseEffects(viewModel: LicenseViewModel) {
}

@Composable
private fun LicenseContent(
    state: LicenseState,
    onSelectLibrary: (Library?) -> Unit,
    modifier: Modifier = Modifier,
) {
    TwoPaneDialogLayout(
        showDetail = state.selectedLibrary != null,
        onDismissDetail = { onSelectLibrary(null) },
        listNode = {
            LibraryList(
                libraries = state.libs,
                onLibrary = onSelectLibrary,
            )
        },
        detailNode = {
            Crossfade(
                state.selectedLibrary,
                label = "library_detail",
            ) { lib ->
                if (lib != null) {
                    LibraryDetail(library = lib)
                }
            }
        },
        emptyNode = {
            NoLibrarySelected()
        },
        modifier = modifier,
    )
}
