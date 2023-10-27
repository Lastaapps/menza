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

package cz.lastaapps.storage.repo

import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

val repoModule = module {
    single<AllergenRepo> { AllergenRepoImpl(get(), get(), Dispatchers.IO) }
    single<ContactsRepo> { ContactsRepoImpl(get(), get(), Dispatchers.IO) }
    single<LocationRepo> { LocationRepoImpl(get(), get(), Dispatchers.IO) }
    single<MenzaRepo> { MenzaRepoImpl(get(), get(), Dispatchers.IO) }
    single<MessagesRepo> { MessagesRepoImpl(get(), get(), Dispatchers.IO) }
    single<OpeningHoursRepo> { OpeningHoursRepoImpl(get(), get(), Dispatchers.IO) }
}
