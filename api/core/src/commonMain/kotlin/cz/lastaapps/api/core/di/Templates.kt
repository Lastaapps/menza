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

package cz.lastaapps.api.core.di

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.repo.InfoRepo
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.repo.TodayDishRepo
import cz.lastaapps.api.core.domain.repo.WeekDishRepo
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

inline fun <reified T : MenzaType> Module.registerMenzaType(
    crossinline menzaRepo: Scope.() -> MenzaRepo,
    crossinline dishRepo: Scope.(T) -> TodayDishRepo,
    crossinline infoRepo: Scope.(T) -> InfoRepo,
    crossinline weekRepo: Scope.(T) -> WeekDishRepo,
) {
    single(named<T>()) { menzaRepo() }

    scope<T> {
        scoped { (menza: T) -> dishRepo(menza) }
        scoped { (menza: T) -> infoRepo(menza) }
        scoped { (menza: T) -> weekRepo(menza) }
    }
}
