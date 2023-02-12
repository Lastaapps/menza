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

package cz.lastaapps.menza.ui.theme

import androidx.compose.ui.graphics.Color
import cz.lastaapps.api.core.domain.model.common.Menza
import kotlin.math.abs
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private val menzaColors = persistentListOf(
    Color(0xfff44333) to Color(0xffff795e),
    Color(0xffe91e63) to Color(0xffff6090),
    Color(0xff9c27b0) to Color(0xffd05ce3),
    Color(0xff673ab7) to Color(0xff9a67ea),
    Color(0xff3f51b5) to Color(0xff757de8),
    Color(0xff2196f3) to Color(0xff6ec6ff),
    Color(0xff03a9f4) to Color(0xff67daff),
    Color(0xff00bcd4) to Color(0xff62efff),
    Color(0xff009688) to Color(0xff52c7b8),
    Color(0xff4caf50) to Color(0xff80e27e),
    Color(0xff8bc34a) to Color(0xffbef67a),
    //Color(0xffcddc39) to Color(0xffffff6e),
    //Color(0xffffeb3b) to Color(0xffffff72),
    Color(0xffffc107) to Color(0xfffff350),
    Color(0xffff9800) to Color(0xffffc947),
    Color(0xffff5722) to Color(0xffff8a50),
)

fun colorForMenza(menza: Menza): ImmutableList<Color> {
    val hash = menza.name.hashCode()
    return menzaColors[abs(hash % menzaColors.size)]
        .let { persistentListOf(it.first, it.second) }
}
