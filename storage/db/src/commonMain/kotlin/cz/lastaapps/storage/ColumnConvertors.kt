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

package cz.lastaapps.storage

import com.squareup.sqldelight.ColumnAdapter
import cz.lastaapps.entity.LocalTime
import cz.lastaapps.entity.allergens.AllergenId
import cz.lastaapps.entity.info.Email
import cz.lastaapps.entity.info.Name
import cz.lastaapps.entity.info.PhoneNumber
import cz.lastaapps.entity.info.Role
import cz.lastaapps.entity.menza.Address
import cz.lastaapps.entity.menza.Coordinates
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.entity.menza.Opened
import kotlinx.datetime.DayOfWeek

internal object ColumnConvertors {

    val allergenId = object : ColumnAdapter<AllergenId, Long> {
        override fun decode(databaseValue: Long) =
            AllergenId(databaseValue.toInt())

        override fun encode(value: AllergenId) = value.id.toLong()
    }

    val menzaId = object : ColumnAdapter<MenzaId, Long> {
        override fun decode(databaseValue: Long) =
            MenzaId(databaseValue.toInt())

        override fun encode(value: MenzaId) = value.id.toLong()
    }

    val opened = object : ColumnAdapter<Opened, Long> {
        override fun decode(databaseValue: Long): Opened {
            Opened.states.forEach {
                if (it.id == databaseValue.toInt()) {
                    return it
                }
            }
            error("Opened state not found")
        }

        override fun encode(value: Opened): Long {
            return value.id.toLong()
        }
    }

    val address = object : ColumnAdapter<Address, String> {
        override fun decode(databaseValue: String) =
            Address(databaseValue)

        override fun encode(value: Address) = value.stringForm
    }

    val coordinates = object : ColumnAdapter<Coordinates, String> {
        override fun decode(databaseValue: String) =
            Coordinates.restoreFromString(databaseValue)

        override fun encode(value: Coordinates) = value.saveToString()
    }

    val dayOfWeek = object : ColumnAdapter<DayOfWeek, Long> {
        override fun decode(databaseValue: Long) =
            DayOfWeek.of(databaseValue.toInt())

        override fun encode(value: DayOfWeek) = value.value.toLong()
    }

    val localTime = object : ColumnAdapter<LocalTime, Long> {
        override fun decode(databaseValue: Long): LocalTime {
            return LocalTime.fromSeconds(databaseValue.toInt())
        }

        override fun encode(value: LocalTime): Long {
            return value.toSeconds().toLong()
        }
    }

    val name = object : ColumnAdapter<Name, String> {
        override fun decode(databaseValue: String): Name {
            return Name(databaseValue)
        }

        override fun encode(value: Name): String {
            return value.name
        }
    }

    val role = object : ColumnAdapter<Role, String> {
        override fun decode(databaseValue: String): Role {
            return Role(databaseValue)
        }

        override fun encode(value: Role): String {
            return value.role
        }
    }

    val phone = object : ColumnAdapter<PhoneNumber, String> {
        override fun decode(databaseValue: String): PhoneNumber {
            return PhoneNumber(databaseValue)
        }

        override fun encode(value: PhoneNumber): String {
            return value.phone
        }
    }

    val email = object : ColumnAdapter<Email, String> {
        override fun decode(databaseValue: String): Email {
            return Email(databaseValue)
        }

        override fun encode(value: Email): String {
            return value.mail
        }
    }
}
