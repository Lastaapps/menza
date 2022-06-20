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

package cz.lastaapps.menza.ui.dests.week

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.lastaapps.entity.common.CourseType
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.week.WeekDish
import cz.lastaapps.menza.compareToLocal
import cz.lastaapps.menza.di.WeekRepoFactory
import cz.lastaapps.storage.repo.MenzaError
import cz.lastaapps.storage.repo.WeekRepo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import java.util.*

class WeekViewModel constructor(
    private val weekRepoFactory: WeekRepoFactory,
) : ViewModel() {

    private val repos = HashMap<MenzaId, WeekRepo>()
    private val cache = HashMap<MenzaId, MutableStateFlow<List<DayDishList>>>()
    val errors = Channel<MenzaError>(Channel.BUFFERED)

    fun getData(menzaId: MenzaId, locale: Locale): StateFlow<List<DayDishList>> {

        val data = cache[menzaId]
        if (data != null) return data

        return MutableStateFlow<List<DayDishList>>(emptyList()).also {
            cache[menzaId] = it
            refresh(menzaId, locale)
        }
    }

    fun isRefreshing(menzaId: MenzaId): StateFlow<Boolean> {
        return repos.getOrCreate(menzaId).requestInProgress
    }

    fun refresh(menzaId: MenzaId, locale: Locale) {
        viewModelScope.launch {

            val repo = repos.getOrCreate(menzaId)

            val data = repo.getData()

            if (data != null) {
                cache.getOrPut(menzaId) { MutableStateFlow(emptyList()) }.value =
                    data.toDayDishList(locale)
            }
            while (true)
                repo.errors.tryReceive().takeIf { it.isSuccess }
                    ?.let { errors.send(it.getOrThrow()) } ?: break
        }
    }

    private fun HashMap<MenzaId, WeekRepo>.getOrCreate(menzaId: MenzaId) = getOrPut(menzaId) {
        weekRepoFactory.create(menzaId)
    }

    private fun Collection<WeekDish>.toDayDishList(locale: Locale): List<DayDishList> {
        val map = mutableMapOf<LocalDate, MutableList<WeekDish>>()

        //group of days
        forEach {
            map.getOrPut(it.date) { mutableListOf() }.add(it)
        }

        return map.entries.map { pair ->
            //group of courses
            val courses = mutableMapOf<CourseType, MutableList<WeekDish>>()
            pair.value.forEach { dish ->
                courses.getOrPut(dish.courseType) { mutableListOf() }.add(dish)
            }

            val sortedCourses = courses.entries.sortedWith { c1, c2 ->
                //sorting courses
                c1.key.webOrder.compareTo(c2.key.webOrder).takeUnless { it == 0 }
                    ?: c1.key.type.compareToLocal(c2.key.type, locale)
            }.map {
                //sorting dish for a course
                it.value.sortWith { d1, d2 ->
                    d1.name.compareToLocal(d2.name, locale)
                }
                it.key to it.value
            }

            pair.key to sortedCourses
        }
            .sortedBy { it.first }
            .map { DayDishList(it.first, it.second) }
    }


}