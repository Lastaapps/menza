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

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import cz.lastaapps.api.core.domain.model.HashType
import cz.lastaapps.menza.api.agata.domain.HashStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class HashStoreImpl(
    private val settings: Settings,
) : HashStore {

    private val mutex = Mutex()

    override suspend fun storeHash(type: HashType, hash: String) = mutex.withLock {
        settings[type.func] = hash
    }

    override suspend fun shouldReload(type: HashType, hash: String): Boolean = mutex.withLock {
        settings.getStringOrNull(type.func) != hash
    }
}
