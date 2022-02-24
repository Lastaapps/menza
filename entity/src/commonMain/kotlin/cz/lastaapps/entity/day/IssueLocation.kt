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

package cz.lastaapps.entity.day

/**
 * Holds info about issue place
 * on the web this info is located fight next to the dish price
 * For example for Podolí Menza
 * terminalId: 21
 * windowId: 3
 * abbrev: J
 * name: Jídelna
 * https://agata.suz.cvut.cz/jidelnicky/index.php?clPodsystem=4
 */
data class IssueLocation(
    val terminalId: Int,
    val windowsId: Int,
    val abbrev: String,
    val name: String,
) {
    init {
        require(terminalId >= 0) { "Terminal id is negative $terminalId" }
        require(windowsId >= 0) { "Window id is negative $windowsId" }
        require(abbrev.isNotBlank()) { "Issue location abbrev is blank" }
        require(name.isNotBlank()) { "Issue location abbrev is blank" }
    }
}