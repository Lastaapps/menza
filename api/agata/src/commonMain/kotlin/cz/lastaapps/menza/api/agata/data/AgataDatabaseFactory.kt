/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

import agata.AddressEntity
import agata.ContactEntity
import agata.DishEntity
import agata.DishTypeEntity
import agata.InfoEntity
import agata.LinkEntity
import agata.NewsEntity
import agata.OpenTimeEntity
import agata.PictogramEntity
import agata.ServingPlaceEntity
import agata.StrahovEntity
import agata.SubsystemEntity
import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import cz.lastaapps.api.agata.AgataDatabase
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.api.agata.data.model.DBLang
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@JvmInline
internal value class AgataDatabaseSqlDriver(
    val sqlDriver: SqlDriver,
)

internal object AgataDatabaseFactory {
    private val log = localLogger()

    fun createDatabase(driver: AgataDatabaseSqlDriver) =
        AgataDatabase
            .invoke(
                driver.sqlDriver,
                AddressEntityAdapter =
                    AddressEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                ContactEntityAdapter =
                    ContactEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                DishEntityAdapter =
                    DishEntity.Adapter(
                        allergensAdapter = LongListAdapter,
                        servingPlacesAdapter = LongListAdapter,
                        pictogramAdapter = LongListAdapter,
                        languageAdapter = LanguageAdapter,
                    ),
                DishTypeEntityAdapter =
                    DishTypeEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                InfoEntityAdapter =
                    InfoEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                LinkEntityAdapter =
                    LinkEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                NewsEntityAdapter =
                    NewsEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                OpenTimeEntityAdapter =
                    OpenTimeEntity.Adapter(
                        dayFromAdapter = DayOfWeekAdapter,
                        dayToAdapter = DayOfWeekAdapter,
                        timeFromAdapter = LocalTimeAdapter,
                        timeToAdapter = LocalTimeAdapter,
                        languageAdapter = LanguageAdapter,
                    ),
                PictogramEntityAdapter =
                    PictogramEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                ServingPlaceEntityAdapter =
                    ServingPlaceEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
                StrahovEntityAdapter =
                    StrahovEntity.Adapter(
                        allergensAdapter = LongListAdapter,
                        languageAdapter = LanguageAdapter,
                    ),
                SubsystemEntityAdapter =
                    SubsystemEntity.Adapter(
                        languageAdapter = LanguageAdapter,
                    ),
            ).also { log.i { "Database created/loaded" } }
}

private object LongListAdapter : ColumnAdapter<List<Long>, String> {
    private const val DELIMITER = ";"

    override fun decode(databaseValue: String): List<Long> =
        databaseValue
            .split(DELIMITER)
            .filter { it.isNotEmpty() }
            .map { item -> item.toLong() }

    override fun encode(value: List<Long>): String = value.joinToString(separator = DELIMITER)
}

private object DayOfWeekAdapter : ColumnAdapter<DayOfWeek, Long> {
    override fun decode(databaseValue: Long): DayOfWeek = DayOfWeek.of(databaseValue.toInt())

    override fun encode(value: DayOfWeek): Long = value.value.toLong()
}

private object LocalTimeAdapter : ColumnAdapter<LocalTime, Long> {
    override fun decode(databaseValue: Long): LocalTime = LocalTime.fromSecondOfDay(databaseValue.toInt())

    override fun encode(value: LocalTime): Long = value.toSecondOfDay().toLong()
}

private object LanguageAdapter : ColumnAdapter<DBLang, String> {
    override fun decode(databaseValue: String): DBLang = DBLang.entries.first { it.value == databaseValue }

    override fun encode(value: DBLang): String = value.value
}
