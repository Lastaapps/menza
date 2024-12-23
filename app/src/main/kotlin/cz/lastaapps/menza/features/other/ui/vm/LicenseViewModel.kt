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

package cz.lastaapps.menza.features.other.ui.vm

import com.mikepenz.aboutlibraries.entity.Library
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.other.domain.usecase.GetLibrariesUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

internal class LicenseViewModel(
    context: VMContext,
    private val getLibs: GetLibrariesUC,
) : StateViewModel<LicenseState>(LicenseState(), context) {
    override suspend fun onFirstAppearance() {
        val lib = getLibs()

        // to filter out wrongly named libraries
        lib.libraries
            .filter { !it.name.startsWith("$") }
            .toImmutableList()
            .let { libs ->
                updateState { copy(libs = libs) }
            }
    }

    fun selectLibrary(library: Library?) {
        updateState { copy(selectedLibrary = library) }
    }
}

internal data class LicenseState(
    val libs: ImmutableList<Library> = persistentListOf(),
    val selectedLibrary: Library? = null,
) : VMState
