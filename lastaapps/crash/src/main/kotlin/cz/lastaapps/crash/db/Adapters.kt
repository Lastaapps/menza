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

package cz.lastaapps.crash.db

import app.cash.sqldelight.ColumnAdapter
import cz.lastaapps.crash.entity.ErrorSeverity
import cz.lastaapps.crash.entity.ReportState
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object CrashAdapter {

    val dateAdapter = object : ColumnAdapter<ZonedDateTime, String> {
        private val format = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        override fun decode(databaseValue: String): ZonedDateTime =
            ZonedDateTime.of(LocalDateTime.parse(databaseValue, format), ZoneId.of("UTC"))

        override fun encode(value: ZonedDateTime): String =
            value.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime().format(format)
    }
    val severityAdapter = object : ColumnAdapter<ErrorSeverity, Long> {
        override fun decode(databaseValue: Long): ErrorSeverity {
            return ErrorSeverity.values().find { it.id == databaseValue.toByte() }
                ?: error("Error severity not found: $databaseValue")
        }

        override fun encode(value: ErrorSeverity): Long = value.id.toLong()
    }
    val reportedAdapter = object : ColumnAdapter<ReportState, Long> {
        override fun decode(databaseValue: Long): ReportState {
            return ReportState.values().find { it.id == databaseValue.toByte() }
                ?: error("Report state not found: $databaseValue")
        }

        override fun encode(value: ReportState): Long = value.id.toLong()
    }
}