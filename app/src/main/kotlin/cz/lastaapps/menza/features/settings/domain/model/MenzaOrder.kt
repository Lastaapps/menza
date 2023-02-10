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

package cz.lastaapps.menza.features.settings.domain.model

data class MenzaOrder(
    val order: Int,
    val visible: Boolean,
) : Comparable<MenzaOrder> {
    override fun compareTo(other: MenzaOrder): Int =
        visible.compareTo(other.visible).let { -1 * it }.takeUnless { it == 0 }
            ?: order.compareTo(other.order)

    companion object {
        val largest get() = MenzaOrder(Int.MAX_VALUE, false)
    }
}
