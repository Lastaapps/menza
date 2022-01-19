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

package cz.lastaapps.menza.ui.dish

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.LocalWindowWidth
import cz.lastaapps.menza.ui.WindowSizeClass
import cz.lastaapps.menza.ui.today.TodayDishList
import cz.lastaapps.menza.ui.week.WeekDishList

@Composable
fun DishContent(menzaId: MenzaId?, modifier: Modifier = Modifier) {
    Surface(modifier, color = MaterialTheme.colorScheme.secondary) {
        if (LocalWindowWidth.current == WindowSizeClass.COMPACT) {
            WeekDishList(menzaId = menzaId, modifier = Modifier.fillMaxHeight())
            /*TodayDishList(
                menzaId = menzaId,
                modifier = Modifier
                    .fillMaxHeight(),
            )*/
        } else {
            Row {
                TodayDishList(
                    menzaId = menzaId,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                )
                WeekDishList(
                    menzaId = menzaId,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f),
                )
            }
        }
    }
}
