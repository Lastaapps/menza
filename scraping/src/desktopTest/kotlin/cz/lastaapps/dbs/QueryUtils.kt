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

package cz.lastaapps.dbs

import cz.lastaapps.entity.LocalTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

object QueryUtils {

    fun insert(table: String, params: Collection<String>, values: Collection<Any?>) =
        InsertQuery(table, params, values).printOut()

    fun resetTimer(table: String, column: String) =
        SerialSetMaxValue(table, column).printOut()

    private fun Query.printOut() = println("${this.resolve()};")
}

interface Query {
    /** @return raw query withnout () or ;*/
    fun resolve(): String
}

class InsertQuery(val table: String, val params: Collection<String>, val values: Collection<Any?>) :
    Query {
    override fun resolve(): String {
        val parStr = params.joinToString(separator = ", ") { it.lowercase() }
        val valStr = values.joinToString(separator = ", ") {
            when (it) {
                is String -> "'$it'"
                is Boolean -> if (it) "TRUE" else "FALSE"
                is Int -> it.toString()
                is Double -> it.toString()
                is DayOfWeek -> it.value.toString()
                is LocalTime -> "'%02d:%02d'".format(it.hours, it.minutes)
                is LocalDate -> "'" + it.toJavaLocalDate()
                    .format(DateTimeFormatter.ISO_LOCAL_DATE) + "'"
                is Query -> "(${it.resolve()})"
                null -> "NULL"
                else -> error("Unknown type - ${it::class.simpleName}")
            }
        }
        return "INSERT INTO $table ($parStr) VALUES ($valStr)"
    }
}

class SerialLastValue(val table: String, val column: String) : Query {
    override fun resolve(): String = "SELECT currval(${SerialQuery(table, column).resolve()})"
}

class SerialSetMaxValue(val table: String, val column: String) : Query {
    override fun resolve(): String {
        val max = "SELECT MAX($column) FROM $table"
        return "SELECT setval(${SerialQuery(table, column).resolve()}, ($max))"
    }
}

class SerialQuery(val table: String, val column: String) : Query {
    override fun resolve(): String = "pg_get_serial_sequence('$table', '$column')"
}
