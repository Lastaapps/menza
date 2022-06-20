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

package cz.lastaapps.storage.repo

import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

val DIModule = DI.Module("repo") {
    import(cz.lastaapps.storage.db.DIModule)

    bindSingleton<AllergenRepo> { AllergenRepoImpl(instance(), instance(), Dispatchers.IO) }
    bindSingleton<ContactsRepo> { ContactsRepoImpl(instance(), instance(), Dispatchers.IO) }
    bindSingleton<LocationRepo> { LocationRepoImpl(instance(), instance(), Dispatchers.IO) }
    bindSingleton<MenzaRepo> { MenzaRepoImpl(instance(), instance(), Dispatchers.IO) }
    bindSingleton<MessagesRepo> { MessagesRepoImpl(instance(), instance(), Dispatchers.IO) }
    bindSingleton<OpeningHoursRepo> { OpeningHoursRepoImpl(instance(), instance(), Dispatchers.IO) }
}