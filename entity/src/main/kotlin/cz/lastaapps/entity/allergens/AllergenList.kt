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

package cz.lastaapps.entity.allergens

import kotlin.math.pow

@JvmInline
value class AllergenList(
    val allergens: Int
) {

    companion object {
        // the number of current allergens, full Int is overall 32 bits long
        internal val range = 14
    }

    val boolArray: BooleanArray
        get() {
            val array = BooleanArray(range)

            var remaining = allergens
            for (i in 0 until range) {
                array[i] = remaining % 2 == 1
                remaining /= 2
            }

            return array
        }

    val allergenIdSet: Set<Int>
        get() {
            return boolArray.mapIndexed() { index, state ->
                if (state) index + 1 else null
            }.filterNotNull().toSet()
        }
}

//fun Collection<Allergen>.toAllergenList(): AllergenList {
//    return this.map { it.id.id }.toAllergenList()
//}

fun Collection<Int>.toAllergenList(): AllergenList {
    var output = 0

    for (i in 0 until AllergenList.range) {
        if (this.contains(i + 1))
            output += 2.0.pow(i).toInt()
    }
    return AllergenList(output)
}
