/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.api.core.domain.model.dish

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class DishCategory(
    val nameShort: String?,
    val name: String?,
    val dishList: ImmutableList<Dish>,
) {
    val someName get() = name ?: nameShort

    companion object {
        fun other(dishList: ImmutableList<Dish>) = DishCategory(null, null, dishList)
    }

    @Suppress("SpellCheckingInspection")
    object Mock {
        val soups =
            DishCategory(
                "Polévky",
                "Patoky všeho druhu",
                persistentListOf(
                    Dish.Mock.soupSekerkova,
                    Dish.Mock.soupJezeckova,
                ),
            )
        val babica =
            DishCategory(
                "Babica",
                "Mňamky Jirky Babici",
                persistentListOf(
                    Dish.Mock.dishHnedaOmacka,
                    Dish.Mock.dishKuratko,
                    Dish.Mock.dishPtacek,
                    Dish.Mock.dishNoky,
                ),
            )
        val chalky =
            DishCategory(
                "Chlálky",
                "Dlabance dne",
                persistentListOf(
                    Dish.Mock.dishTux,
                    Dish.Mock.dishKunda,
                    Dish.Mock.dishMaxipes,
                    Dish.Mock.dishMisterious,
                ),
            )
        val dezerty =
            DishCategory(
                "Dezerty",
                "Sekce pro tlustá prasátka",
                persistentListOf(
                    Dish.Mock.desertKrtkuvDort,
                ),
            )

        val allCathegories = persistentListOf(soups, babica, chalky, dezerty)
        val empty = persistentListOf<DishCategory>()
    }
}
