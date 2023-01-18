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

package cz.lastaapps.menza.api.agata.data

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.menza.api.agata.api.CafeteriaApi
import cz.lastaapps.menza.api.agata.api.DishApi
import cz.lastaapps.menza.api.agata.api.SubsystemApi
import cz.lastaapps.menza.api.agata.domain.AgataRepo
import cz.lastaapps.menza.api.agata.domain.HashStore
import cz.lastaapps.menza.api.agata.domain.model.common.DishCategory
import cz.lastaapps.menza.api.agata.domain.model.common.Info
import cz.lastaapps.menza.api.agata.domain.model.common.Menza
import cz.lastaapps.menza.api.agata.domain.model.common.WeekDayDish
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

internal class AgataRepoImpl(
    private val hashStore: HashStore,
    private val cafeteriaApi: CafeteriaApi,
    private val dishApi: DishApi,
    private val subsystemApi: SubsystemApi,
//    private val db: ...
) : AgataRepo {
    override fun getIsReady(): Flow<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun syncRepo(): Outcome<Unit> {
        TODO("Not yet implemented")
    }

    override fun getMenzaList(): Flow<ImmutableList<Menza>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncMenzas(force: Boolean): Outcome<Unit> {
        TODO("Not yet implemented")
    }

    override fun getDishListFor(menza: Menza): Flow<ImmutableList<DishCategory>> {
        TODO("Not yet implemented")
    }

    override suspend fun syncDishList(menza: Menza, force: Boolean): Outcome<Unit> {
        TODO("Not yet implemented")
    }

    override fun getInfo(menza: Menza): Flow<Info> {
        TODO("Not yet implemented")
    }

    override suspend fun syncInfo(menza: Menza, force: Boolean): Outcome<Unit> {
        TODO("Not yet implemented")
    }

    override fun getWeek(date: LocalDate): Outcome<WeekDayDish> {
        TODO("Not yet implemented")
    }

}
