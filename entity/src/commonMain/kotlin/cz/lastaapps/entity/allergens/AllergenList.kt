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

package cz.lastaapps.entity.allergens

import kotlin.math.pow

/**
 * Holds a list of allergens
 * stores it in binary form, so it can be saved into a database easily
 *
 * Unless dishes are stored in cache database, this is useless
 */
@JvmInline
value class AllergenList internal constructor(
    val allergens: Int
) {

    companion object {
        // the number of current allergens, full Int is overall 32 bits long
        internal const val range = 14

        fun fromAllergenList(list: Collection<Allergen>): AllergenList {
            return fromAllergenIdList(list.map { it.id })
        }

        fun fromAllergenIdList(list: Collection<AllergenId>): AllergenList {
            var output = 0

            for (i in 0 until range) {
                if (list.contains(AllergenId(i + 1)))
                    output += 2.0.pow(i).toInt()
            }
            return AllergenList(output)
        }

        fun fromBoolArray(array: BooleanArray): AllergenList {
            var output = 0

            for (i in 0 until range) {
                if (array[i])
                    output += 2.0.pow(i).toInt()
            }
            return AllergenList(output)
        }
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

    val allergenIdSet: Set<AllergenId>
        get() {
            return boolArray.mapIndexed { index, state ->
                if (state) index + 1 else null
            }.filterNotNull().map { AllergenId(it) }.toSet()
        }
}
