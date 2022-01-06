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

import cz.lastaapps.entity.common.Amount
import cz.lastaapps.entity.common.FoodType
import cz.lastaapps.entity.common.Price
import cz.lastaapps.entity.menza.MenzaId
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank

data class Food(
    val menzaId: MenzaId,
    val foodType: FoodType,
    val amount: Amount?,
    val name: String,
    val allergenFoodId: FoodAllergens,
    val imageUrl: String?,
    val priceStudent: Price,
    val priceNormal: Price,
    val issuePlaces: List<IssueLocation>,
) {
    init {
        name.shouldNotBeBlank()
        issuePlaces.shouldNotBeEmpty()
    }
}