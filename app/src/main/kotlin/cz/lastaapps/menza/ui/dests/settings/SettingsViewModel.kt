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

package cz.lastaapps.menza.ui.dests.settings

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.model.PriceType
import cz.lastaapps.storage.repo.AllergenRepo
import cz.lastaapps.storage.repo.ContactsRepo
import cz.lastaapps.storage.repo.LocationRepo
import cz.lastaapps.storage.repo.MenzaRepo
import cz.lastaapps.storage.repo.MessagesRepo
import cz.lastaapps.storage.repo.OpeningHoursRepo
import kotlin.system.exitProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel constructor(
    private val app: Application,
    private val allergenRepo: AllergenRepo,
    private val locationRepo: LocationRepo,
    private val messagesRepo: MessagesRepo,
    private val menzaRepo: MenzaRepo,
    private val contactsRepo: ContactsRepo,
    private val openingHoursRepo: OpeningHoursRepo,
) : ViewModel() {

    val aboutShown = MutableStateFlow(false)
    fun showAbout(value: Boolean) {
        aboutShown.value = value
    }

    fun setDarkMode(mode: DarkMode) {
        viewModelScope.launch {
//            sett.setDarkMode(mode)
        }
    }

    fun setUseSystemTheme(mode: Boolean) {
        viewModelScope.launch {
//            sett.setAppTheme(if (mode) AppThemeType.System else AppThemeType.Agata)
        }
    }

    fun setPriceType(type: PriceType) {
        viewModelScope.launch {
//            sett.setPriceType(type)
        }
    }

    fun setPreferredMenza(menzaId: MenzaId) {
        viewModelScope.launch {
//            sett.setPreferredMenza(menzaId)
        }
    }

    fun setLatestMenza(menzaId: MenzaId) {
        viewModelScope.launch {
//            sett.setLatestMenza(menzaId)
        }
    }

    fun setImagesOnMetered(enabled: Boolean) {
        viewModelScope.launch {
//            sett.setImagesOnMetered(enabled)
        }
    }

    fun setImageSize(ration: Float) {
//        viewModelScope.launch { sett.setImageSize(ration) }
    }

    fun setSettingsEverOpened(enabled: Boolean) {
//        viewModelScope.launch { sett.setSettingsEverOpened(enabled) }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun fullRefresh() {
        viewModelScope.launch {

            listOf(
                allergenRepo,
                locationRepo,
                messagesRepo,
                menzaRepo,
                contactsRepo,
                openingHoursRepo
            ).forEach {
                it.clearData()
            }
            app.imageLoader.diskCache?.clear()

            withContext(Dispatchers.Main) {
                exitProcess(0)
            }
        }
    }
}
