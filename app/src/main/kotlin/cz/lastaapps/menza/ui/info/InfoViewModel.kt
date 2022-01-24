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

package cz.lastaapps.menza.ui.info

import androidx.lifecycle.ViewModel
import cz.lastaapps.entity.index
import cz.lastaapps.entity.info.Contact
import cz.lastaapps.entity.info.OpeningHours
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.MenzaLocation
import cz.lastaapps.entity.menza.Message
import cz.lastaapps.storage.repo.ContactsRepo
import cz.lastaapps.storage.repo.LocationRepo
import cz.lastaapps.storage.repo.MessagesRepo
import cz.lastaapps.storage.repo.OpeningHoursRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val messageRepo: MessagesRepo,
    private val contactsRepo: ContactsRepo,
    private val locationRepo: LocationRepo,
    private val openingHoursRepo: OpeningHoursRepo,
) : ViewModel() {

    fun getMessage(menzaId: MenzaId): Flow<List<Message>> {
        return messageRepo.getMessage(menzaId)
    }

    fun getContacts(menzaId: MenzaId): Flow<List<Contact>> {
        return contactsRepo.getContactsForMenza(menzaId)
    }

    fun getLocation(menzaId: MenzaId): Flow<List<MenzaLocation>> {
        return locationRepo.getMenzaLocation(menzaId)
    }

    fun getOpeningHours(menzaId: MenzaId): Flow<List<OpeningLocation>> {
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
                OpeningLocation(pair.first, pair.second)
            }
        }
    }
}
