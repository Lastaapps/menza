/*
 *    Copyright 2021, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.storage

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.runtime.coroutines.asFlow
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.db.MenzaDatabase
import menza.ContactEntity
import menza.MenzaEntity
import menza.OpenedHoursEntity

expect class MenzaDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: MenzaDriverFactory): MenzaDatabase {
    val driver = driverFactory.createDriver()
    return MenzaDatabase(
        driver,
        menzaEntityAdapter = MenzaEntity.Adapter(
            idAdapter = ColumnConvertors.menzaId,
            openedAdapter = ColumnConvertors.opened,
            addressAdapter = ColumnConvertors.address,
            locationAdapter = ColumnConvertors.location,
        ),
        contactEntityAdapter = ContactEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
        ),
        openedHoursEntityAdapter = OpenedHoursEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
            day_of_weekAdapter = ColumnConvertors.dayOfWeek,
            open_Adapter = ColumnConvertors.localTime,
            closeAdapter = ColumnConvertors.localTime,
        ),
}