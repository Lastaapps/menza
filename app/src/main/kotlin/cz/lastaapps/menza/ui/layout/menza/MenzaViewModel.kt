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

package cz.lastaapps.menza.ui.layout.menza

import android.app.Application
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.entity.menza.Menza
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.compareToLocal
import cz.lastaapps.menza.ui.dests.settings.store.*
import cz.lastaapps.storage.repo.MenzaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import javax.inject.Inject

@HiltViewModel
class MenzaViewModel @Inject constructor(
    private val app: Application,
    private val menzaRepo: MenzaRepo,
    private val sett: SettingsStore,
    private val menzaOrder: MenzaOrderDataStore,
) : ViewModel() {

    companion object {
        private val log = logging()
    }

    val isReady = MutableStateFlow(false)
    lateinit var data: StateFlow<List<Menza>>
    private val orderKey: (Menza) -> String = { "agata_${it.menzaId}" }

    fun getForId(id: MenzaId): Menza? = data.value.firstOrNull { it.menzaId == id }

    private suspend fun CoroutineScope.prepareMenza() {

        var myData: MutableStateFlow<List<Menza>>? = null

        menzaRepo.getData(this).combineOrder().collectLatest { list ->
            if (myData == null) {
                myData = MutableStateFlow(list.sortMenzaList()).also { data = it }

                log.i { "Data ready" }

                val menzaId = when (sett.initMenza.first()) {
                    InitMenza.Ask -> null
                    InitMenza.Remember -> sett.latestMenza.first()
                    InitMenza.Specific -> sett.preferredMenza.first()
                }
                selectMenza(menzaId)

                isReady.value = true
            } else {
                myData!!.value = list.sortMenzaList()
            }
        }
    }

    private fun Flow<List<Menza>>.combineOrder() = channelFlow<Map<Menza, Int>> {
        collectLatest { list ->
            menzaOrder.getItemOrder(list, orderKey).simplify().collectLatest {
                send(it)
            }
        }
    }

    private fun Map<Menza, Int>.sortMenzaList(): List<Menza> {

        @Suppress("DEPRECATION")
        val locale = app.applicationContext.resources.configuration.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) it.locales[0] else it.locale
        }
        return asSequence().map { it.key to it.value }.sortedWith { m1, m2 ->
            m1.second.compareTo(m2.second).takeIf { it != 0 }
                ?: m1.first.shorterName.compareToLocal(m2.first.shorterName, locale)
        }.map { it.first }.toList()
    }

    val selectedMenza: StateFlow<MenzaId?>
        get() = mSelectedMenza

    private val mSelectedMenza = MutableStateFlow<MenzaId?>(null)

    fun selectMenza(menzaId: MenzaId?) {
        mSelectedMenza.value = menzaId
        viewModelScope.launch {
            menzaId?.let { sett.setLatestMenza(it) }
        }
    }

    fun saveNewOrder(newOrder: List<Menza>) {
        viewModelScope.launch {
            menzaOrder.setItemOrder(
                newOrder.mapIndexed { index, menza -> menza to index }.toMap(),
                orderKey
            )
        }
    }

    init {
        viewModelScope.launch {
            prepareMenza()
        }
    }
}