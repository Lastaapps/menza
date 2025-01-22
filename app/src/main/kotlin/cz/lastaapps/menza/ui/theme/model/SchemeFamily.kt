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

package cz.lastaapps.menza.ui.theme.model

import androidx.compose.material3.ColorScheme

data class SchemeFamily(
    val baseLight: ColorScheme,
    val baseDark: ColorScheme,
    val mediumContrastLight: ColorScheme,
    val mediumContrastDark: ColorScheme,
    val highContrastLight: ColorScheme,
    val highContrastDark: ColorScheme,
) {
    fun getSchema(
        isLight: Boolean,
        contrastClass: ContrastClass = ContrastClass.NONE,
    ) = when (contrastClass) {
        ContrastClass.NONE ->
            if (isLight) {
                baseLight
            } else {
                baseDark
            }

        ContrastClass.MEDIUM ->
            if (isLight) {
                mediumContrastLight
            } else {
                mediumContrastDark
            }

        ContrastClass.HIGH ->
            if (isLight) {
                highContrastLight
            } else {
                highContrastDark
            }
    }
}
