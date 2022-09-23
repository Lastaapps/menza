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

package cz.lastaapps.menza.ui.dests.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.entity.index
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.info.OpeningHours
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.MenzaLocation
import cz.lastaapps.entity.menza.Message
import cz.lastaapps.storage.repo.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InfoViewModel constructor(
    private val messageRepo: MessagesRepo,
    private val contactsRepo: ContactsRepo,
    private val locationRepo: LocationRepo,
    private val openingHoursRepo: OpeningHoursRepo,
) : ViewModel() {

    val errors = Channel<MenzaError>(Channel.BUFFERED)

    val isRefreshing: StateFlow<Boolean> get() = mIsRefreshing
    private val mIsRefreshing = MutableStateFlow(false)

    init {
        fun Flow<MenzaError>.moveToChannel() {
            viewModelScope.launch {
                this@moveToChannel.collect { errors.send(it) }
            }
        }
        messageRepo.errors.receiveAsFlow().moveToChannel()
        contactsRepo.errors.receiveAsFlow().moveToChannel()
        locationRepo.errors.receiveAsFlow().moveToChannel()
        openingHoursRepo.errors.receiveAsFlow().moveToChannel()

        viewModelScope.launch {
            combine(
                messageRepo.requestInProgress,
                contactsRepo.requestInProgress,
                locationRepo.requestInProgress,
                openingHoursRepo.requestInProgress,
            ) { b0, b1, b2, b3 ->
                println("FIND ME: $b0 $b1 $b2 $b3")
                b0 || b1 || b2 || b3 }.collectLatest {
                mIsRefreshing.emit(it)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            messageRepo.refreshData().filterNotNull().first()
                    && contactsRepo.refreshData().filterNotNull().first()
                    && locationRepo.refreshData().filterNotNull().first()
                    && openingHoursRepo.refreshData().filterNotNull().first()
        }
    }

    fun getMessage(menzaId: MenzaId): Flow<ImmutableList<Message>> {
        return messageRepo.getMessage(menzaId).map { it.toImmutableList() }
    }

    fun getContacts(menzaId: MenzaId): Flow<ImmutableList<Contact>> {
        return contactsRepo.getContactsForMenza(menzaId).map { it.toImmutableList() }
    }

    fun getLocation(menzaId: MenzaId): Flow<ImmutableList<MenzaLocation>> {
        return locationRepo.getMenzaLocation(menzaId).map { it.toImmutableList() }
    }

    fun getOpeningHours(menzaId: MenzaId): Flow<ImmutableList<OpeningLocation>> {
        return openingHoursRepo.getForMenza(menzaId).map { input ->
            val places = mutableMapOf<String, MutableList<OpeningHours>>()
            input.forEach {
                places.getOrPut(it.locationName) { mutableListOf() }.add(it)
            }
            val combined = places.entries.map { entry ->

                entry.key to entry.value.sortedWith { e1, e2 ->
                    e1.comment?.compareTo(e2.comment ?: "").takeIf { it != 0 }
                        ?: e1.dayOfWeek.index.compareTo(e2.dayOfWeek.index)
                }.let { list ->
                    if (list.isEmpty()) return@let emptyList()
                    val sameGroups = mutableListOf(mutableListOf(0))

                    for (i in 0 until list.size - 1) {
                        val cur = list[i]
                        val next = list[i + 1]
                        val areAfter = next.dayOfWeek.index - cur.dayOfWeek.index == 1
                        val times = cur.open == next.open && cur.close == next.close
                        val comment = cur.comment == next.comment

                        if (areAfter && times && comment) {
                            sameGroups.last().add(i + 1)
                        } else {
                            sameGroups.add(mutableListOf(i + 1))
                        }
                    }

                    sameGroups.map { group ->
                        val start = list[group.first()]
                        val end = list[group.last()]
                        OpeningInterval(
                            start.dayOfWeek,
                            end.dayOfWeek,
                            start.open,
                            start.close,
                            start.comment
                        )
                    }
                }
            }
            combined.map { pair ->
                OpeningLocation(pair.first, pair.second.toImmutableList())
            }
        }.map { it.toImmutableList() }
    }
}
