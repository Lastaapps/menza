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

package cz.lastaapps.entity.day

import io.kotest.matchers.ints.shouldBeNonNegative
import io.kotest.matchers.string.shouldNotBeBlank

data class IssueLocation(
    val terminalId: Int,
    val windowsId: Int,
    val abbrev: String,
    val name: String,
) {
    init {
        terminalId.shouldBeNonNegative()
        windowsId.shouldBeNonNegative()
        abbrev.shouldNotBeBlank()
        name.shouldNotBeBlank()
    }
}