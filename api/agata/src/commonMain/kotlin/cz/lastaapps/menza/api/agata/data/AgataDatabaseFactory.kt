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

import agata.DishEntity
import agata.OpenTimeEntity
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import cz.lastaapps.api.agata.AgataDatabase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@JvmInline
internal value class AgataDatabaseSqlDriver(val sqlDriver: SqlDriver)

internal object AgataDatabaseFactory {

    fun createDatabase(
        driver: AgataDatabaseSqlDriver,
    ) = AgataDatabase.invoke(
        driver.sqlDriver,
        DishEntityAdapter = DishEntity.Adapter(
            allergensAdapter = AllergensAdapter,
        ),
        OpenTimeEntityAdapter = OpenTimeEntity.Adapter(
            dayFromAdapter = DayOfWeekAdapter,
            dayToAdapter = DayOfWeekAdapter,
            timeFromAdapter = LocalTimeAdapter,
            timeToAdapter = LocalTimeAdapter,
        ),
    )
}

private object AllergensAdapter : ColumnAdapter<ImmutableList<Int>, String> {
    private const val delimiter = ";"

    override fun decode(databaseValue: String): ImmutableList<Int> =
        databaseValue.split(delimiter).map { it.toInt() }.toImmutableList()

    override fun encode(value: ImmutableList<Int>): String =
        value.joinToString(separator = delimiter)
}

private object DayOfWeekAdapter : ColumnAdapter<DayOfWeek, Long> {
    override fun decode(databaseValue: Long): DayOfWeek =
        DayOfWeek.of(databaseValue.toInt())

    override fun encode(value: DayOfWeek): Long =
        value.value.toLong()
}

private object LocalTimeAdapter : ColumnAdapter<LocalTime, Long> {
    override fun decode(databaseValue: Long): LocalTime =
        LocalTime.fromSecondOfDay(databaseValue.toInt())

    override fun encode(value: LocalTime): Long =
        value.toSecondOfDay().toLong()
}
