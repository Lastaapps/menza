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

package cz.lastaapps.api.core.domain.validity

@JvmInline
value class ValidityKey private constructor(val name: String) {

    companion object {
        fun agataToday(subsystemId: Int) = ValidityKey("agata_today_$subsystemId")
        fun agataInfo(subsystemId: Int) = ValidityKey("agata_info_$subsystemId")
        fun agataWeek(subsystemId: Int) = ValidityKey("agata_week_$subsystemId")
        fun agataMenza() = ValidityKey("agata_menza")
        fun strahov() = ValidityKey("strahov")
        fun buffetDish() = ValidityKey("buffet_dish")
    }
}
