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

import android.app.Application
import androidx.lifecycle.ViewModel
import cz.lastaapps.osslicenseaccess.ArtifactLicense
import cz.lastaapps.osslicenseaccess.LicenseLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    val selectedLicense: StateFlow<ArtifactLicense?>
        get() = mSelectedLicense
    private val mSelectedLicense = MutableStateFlow<ArtifactLicense?>(null)

    fun selectLicense(artifact: ArtifactLicense?) {
        mSelectedLicense.value = artifact
    }

    fun getList() = LicenseLoader.loadLicenses(app).sortedBy { it.name }
    fun getTextForArtifact(artifact: ArtifactLicense) = LicenseLoader.loadLicenseText(app, artifact)

}