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

package cz.lastaapps.entity.menza

/**
 * Represents if a menza is currently open of close (according to web, not current time)
 * agata.suz.cvut.cz/jidelnicky/oteviraci-doby.php
 */
sealed class Opened private constructor(val id: Int) {
    data object OPENED : Opened(1)
    data object CLOSED : Opened(0)
    data object UNKNOWN : Opened(-1)
    data object EXPECTED : Opened(2)

    companion object {
        val states by lazy { listOf(OPENED, CLOSED, UNKNOWN, EXPECTED) }
    }
}
