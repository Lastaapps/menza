/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.api.buffet.data

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import buffet.DishEntity
import cz.lastaapps.api.buffet.BuffetDatabase
import cz.lastaapps.api.buffet.domain.model.BuffetType
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber

@JvmInline
internal value class BuffetDatabaseSqlDriver(
    val sqlDriver: SqlDriver,
)

internal object BuffetDatabaseFactory {
    fun createDatabase(driver: BuffetDatabaseSqlDriver) =
        BuffetDatabase.invoke(
            driver.sqlDriver,
            DishEntityAdapter =
                DishEntity.Adapter(
                    buffetAdapter = BuffetAdapter,
                    dayOfWeekAdapter = DayOfWeekAdapter,
                ),
        )
}

private object BuffetAdapter : ColumnAdapter<BuffetType, String> {
    override fun decode(databaseValue: String): BuffetType = BuffetType.valueOf(databaseValue)

    override fun encode(value: BuffetType): String = value.name
}

private object StringListAdapter : ColumnAdapter<List<String>, String> {
    private const val DELIMITER = ";" // not ideal...

    override fun decode(databaseValue: String): List<String> = databaseValue.split(DELIMITER)

    override fun encode(value: List<String>): String = value.joinToString(separator = DELIMITER)
}

private object DayOfWeekAdapter : ColumnAdapter<DayOfWeek, Long> {
    override fun decode(databaseValue: Long): DayOfWeek = DayOfWeek(databaseValue.toInt())

    override fun encode(value: DayOfWeek): Long = value.isoDayNumber.toLong()
}
