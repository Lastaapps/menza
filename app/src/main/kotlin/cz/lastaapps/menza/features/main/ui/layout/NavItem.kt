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

package cz.lastaapps.menza.features.main.ui.layout

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import cz.lastaapps.menza.R.string

internal enum class NavItem(
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    Today(string.nav_today, Filled.DinnerDining),
    Week(string.nav_week, Filled.MenuBook),
    Info(string.nav_info, Filled.Info),
    Settings(string.nav_settings, Filled.Settings),
}
