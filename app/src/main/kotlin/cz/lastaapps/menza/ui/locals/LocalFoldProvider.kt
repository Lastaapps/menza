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

package cz.lastaapps.menza.ui.locals

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import cz.lastaapps.menza.ui.locals.FoldingClass.NotSupported
import cz.lastaapps.menza.ui.locals.FoldingClass.Supported
import cz.lastaapps.menza.ui.locals.FoldingClass.Unknown
import org.lighthousegames.logging.logging

val LocalFoldProvider = compositionLocalOf<FoldingClass> { Unknown }

private val foldingLog = logging(FoldingClass::class.simpleName)
sealed class FoldingClass private constructor() {
    class Supported(val foldingFeature: FoldingFeature) : FoldingClass()
    data object NotSupported : FoldingClass()
    data object Unknown : FoldingClass()
}

@Composable
fun WithFoldingFeature(
    activity: Activity,
    content: @Composable () -> Unit,
) {
    val folding = rememberFoldingFeature(activity)
    CompositionLocalProvider(LocalFoldProvider provides folding) {
        content()
    }
}

@Composable
private fun rememberFoldingFeature(activity: Activity): FoldingClass {
    val foldingFeatureList by remember(activity) {
        WindowInfoTracker.getOrCreate(activity).windowLayoutInfo(activity)
    }.collectAsState(initial = null)

    if (foldingFeatureList == null)
        return Unknown

    val feature = foldingFeatureList?.displayFeatures?.firstOrNull { it is FoldingFeature }
        ?.let { it as FoldingFeature }

    return if (feature != null) {
        foldingLog.i { "Folding supported: $feature" }
        Supported(feature)
    } else {
        foldingLog.i { "Folding not supported" }
        NotSupported
    }
}
