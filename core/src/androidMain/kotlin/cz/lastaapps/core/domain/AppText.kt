/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.core.domain

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.lastaapps.core.domain.AppText.Formatted

sealed interface AppText {
    operator fun invoke(context: Context): String

    @Composable
    operator fun invoke(): String

    @JvmInline
    value class Rich(
        private val text: String,
    ) : AppText {
        override operator fun invoke(context: Context): String = text

        @Composable
        override operator fun invoke(): String = text
    }

    @JvmInline
    value class Resource(
        @StringRes private val resId: Int,
    ) : AppText {
        override operator fun invoke(context: Context): String = context.getString(resId)

        @Composable
        override operator fun invoke(): String = stringResource(resId)
    }

    class Formatted(
        @StringRes private val resId: Int,
        private val args: Array<out Any>,
    ) : AppText {
        override operator fun invoke(context: Context): String = context.getString(resId, args)

        @Composable
        override operator fun invoke(): String = stringResource(resId, args)
    }
}

@Suppress("FunctionName", "NOTHING_TO_INLINE")
inline fun FormattedVarargs(
    @StringRes resId: Int,
    vararg args: Any,
) = Formatted(resId, args)
