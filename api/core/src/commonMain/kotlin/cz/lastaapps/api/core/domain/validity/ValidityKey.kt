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

package cz.lastaapps.api.core.domain.validity

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.RequestParams

@JvmInline
value class ValidityKey private constructor(
    val name: String,
) {
    fun withLang(lang: String) = ValidityKey(lang + '_' + name)

    fun withMenzaType(menzaType: MenzaType) = ValidityKey(menzaType.id + '_' + name)

    companion object {
        fun agataToday(subsystemId: Int) = ValidityKey("agata_today_$subsystemId")

        fun agataInfo(subsystemId: Int) = ValidityKey("agata_info_$subsystemId")

        fun agataWeek(subsystemId: Int) = ValidityKey("agata_week_$subsystemId")

        fun agataMenza() = ValidityKey("agata_menza")

        fun strahov() = ValidityKey("strahov")

        fun buffetDish() = ValidityKey("buffet_dish")

        fun agataCtuBalance() = ValidityKey("balance_agata_ctu")

        fun rating() = ValidityKey("rating")
    }
}

fun ValidityKey.withParams(params: RequestParams) = withLang(params.language.value)
