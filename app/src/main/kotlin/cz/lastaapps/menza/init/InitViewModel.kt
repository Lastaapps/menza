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

package cz.lastaapps.menza.init

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.menza.init.InitMessage.*
import cz.lastaapps.storage.repo.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("CanBeParameter")
@HiltViewModel
class InitViewModel @Inject constructor(
    private val allergenRepo: AllergenRepo,
    private val contactsRepo: ContactsRepo,
    private val locationRepo: LocationRepo,
    private val menzaRepo: MenzaRepo,
    private val messagesRepo: MessagesRepo,
    private val openingHoursRepo: OpeningHoursRepo,
) : ViewModel() {

    val isDone: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val startedDownloading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val progressMessage: MutableStateFlow<InitMessage> = MutableStateFlow(Preparing)
    val progressIndicator: MutableStateFlow<Float> = MutableStateFlow(0.0f)
    val failed: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val errors: Channel<MenzaError> = Channel(Channel.BUFFERED)

    private val repos = listOf<Pair<GeneralStorageRepo<*>, InitMessage>>(
        allergenRepo to AllergenDone,
        contactsRepo to ContactsDone,
        locationRepo to LocationDone,
        menzaRepo to MenzaDone,
        messagesRepo to MessageDone,
        openingHoursRepo to OpeningHoursDone,
    )

    fun requestRefresh() {
        viewModelScope.launch(Dispatchers.Default) {
            loadDefaultData()
        }
    }

    private suspend fun loadDefaultData() {
        failed.value = false
        progressIndicator.value = 0f
        progressMessage.value = Preparing
        isDone.value = false
        startedDownloading.value = false

        repos.forEachIndexed { index, pair ->
            val repo = pair.first
            progressMessage.value = pair.second

            if (!repo.hasData()) {
                startedDownloading.value = true
                when (repo.refreshData().first()) {
                    true -> Unit
                    false -> {
                        progressMessage.value = Error
                        failed.value = true

                        while (true) {
                            repo.errors.tryReceive().takeIf { it.isSuccess }
                                ?.let { errors.send(it.getOrThrow()) } ?: break
                        }

                        return
                    }
                    null -> error("Refresh already in progress")
                }
            }
            progressIndicator.value = 1f * (index + 1) / repos.size
        }

        progressMessage.value = Done
        if (startedDownloading.value)
            delay(1000)
        isDone.value = true
    }

    init {
        requestRefresh()
    }
}

