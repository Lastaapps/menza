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
import agata.StrahovEntiy
import com.squareup.sqldelight.ColumnAdapter
import com.squareup.sqldelight.db.SqlDriver
import cz.lastaapps.api.agata.AgataDatabase
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import org.lighthousegames.logging.logging

@JvmInline
internal value class AgataDatabaseSqlDriver(val sqlDriver: SqlDriver)

internal object AgataDatabaseFactory {

    private val log = logging()

    fun createDatabase(
        driver: AgataDatabaseSqlDriver,
    ) = AgataDatabase.invoke(
        driver.sqlDriver,
        DishEntityAdapter = DishEntity.Adapter(
            allergensAdapter = LongListAdapter,
            servingPlacesAdapter = LongListAdapter,
            pictogramAdapter = LongListAdapter,
        ),
        OpenTimeEntityAdapter = OpenTimeEntity.Adapter(
            dayFromAdapter = DayOfWeekAdapter,
            dayToAdapter = DayOfWeekAdapter,
            timeFromAdapter = LocalTimeAdapter,
            timeToAdapter = LocalTimeAdapter,
        ),
        StrahovEntiyAdapter = StrahovEntiy.Adapter(
            allergensAdapter = LongListAdapter,
        ),
    ).also { log.i { "Database created/loaded" } }
}

private object LongListAdapter : ColumnAdapter<List<Long>, String> {
    private const val delimiter = ";"

    override fun decode(databaseValue: String): List<Long> =
        databaseValue
            .split(delimiter)
            .filter { it.isNotEmpty() }
            .map { item -> item.toLong() }

    override fun encode(value: List<Long>): String =
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
