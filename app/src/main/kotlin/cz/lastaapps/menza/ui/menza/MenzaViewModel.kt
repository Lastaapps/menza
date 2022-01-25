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

package cz.lastaapps.menza.ui.menza

import android.app.Application
import android.os.Build
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.compareToLocal
import cz.lastaapps.storage.repo.MenzaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import javax.inject.Inject

@HiltViewModel
class MenzaViewModel @Inject constructor(
    private val app: Application,
    private val menzaRepo: MenzaRepo,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    companion object {
        private val log = logging()
        private const val selectedMenzaKey = "menzaId"
    }

    val isReady = MutableStateFlow<Boolean>(false)
    lateinit var data: StateFlow<List<Menza>>

    fun getForId(id: MenzaId): Menza? = data.value.firstOrNull { it.menzaId == id }

    private suspend fun CoroutineScope.prepareMenza() {

        var myData: MutableStateFlow<List<Menza>>? = null

        menzaRepo.getData(this).collectLatest { list ->
            if (myData == null) {
                myData = MutableStateFlow(list.sortMenzaList()).also { data = it }

                log.i { "Data ready" }

                selectMenza(savedStateHandle.get<Int>(selectedMenzaKey)?.let { MenzaId(it) })
                isReady.value = true
            } else {
                myData!!.value = list.sortMenzaList()
            }
        }
    }

    private fun Collection<Menza>.sortMenzaList(): List<Menza> {
        @Suppress("DEPRECATION")
        val locale = app.applicationContext.resources.configuration.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.locales[0] else it.locale
        }
        return sortedWith { m1, m2 ->
            m1.shorterName.compareToLocal(m2.shorterName, locale)
        }
    }

    val selectedMenza: StateFlow<MenzaId?>
        get() = mSelectedMenza

    private val mSelectedMenza = MutableStateFlow<MenzaId?>(null)

    fun selectMenza(menzaId: MenzaId?) {
        mSelectedMenza.value = menzaId
        savedStateHandle[selectedMenzaKey] = menzaId?.id
    }

    init {
        viewModelScope.launch {
            prepareMenza()
        }
    }
}