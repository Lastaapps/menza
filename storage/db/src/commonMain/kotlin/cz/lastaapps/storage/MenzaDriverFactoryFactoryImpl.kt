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
import cz.lastaapps.menza.db.MenzaDatabase
import menza.*

interface MenzaDriverFactory {
    fun createDriver(): SqlDriver
}

expect class MenzaDriverFactoryFactoryImpl : MenzaDriverFactory {
    override fun createDriver(): SqlDriver
}

fun createMenzaDatabase(driverFactory: MenzaDriverFactory): MenzaDatabase {

    val driver = driverFactory.createDriver()

    return MenzaDatabase(
        driver,
        allergenEntityAdapter = AllergenEntity.Adapter(
            idAdapter = ColumnConvertors.allergenId
        ),
        menzaEntityAdapter = MenzaEntity.Adapter(
            idAdapter = ColumnConvertors.menzaId,
            openedAdapter = ColumnConvertors.opened,
        ),
        contactEntityAdapter = ContactEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
            nameAdapter = ColumnConvertors.name,
            roleAdapter = ColumnConvertors.role,
            phoneAdapter = ColumnConvertors.phone,
            emailAdapter = ColumnConvertors.email,
        ),
        openedHoursEntityAdapter = OpenedHoursEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
            day_of_weekAdapter = ColumnConvertors.dayOfWeek,
            open_Adapter = ColumnConvertors.localTime,
            closeAdapter = ColumnConvertors.localTime,
        ),
        locationEntityAdapter = LocationEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
            addressAdapter = ColumnConvertors.address,
            coordinatesAdapter = ColumnConvertors.coordinates,
        ),
        messageEntityAdapter = MessageEntity.Adapter(
            menza_idAdapter = ColumnConvertors.menzaId,
        ),
    )
}