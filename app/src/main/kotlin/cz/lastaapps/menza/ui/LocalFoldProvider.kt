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

package cz.lastaapps.menza.ui

import android.app.Activity
import androidx.compose.runtime.*
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import org.lighthousegames.logging.logging

val LocalFoldProvider = compositionLocalOf<FoldingClass> { FoldingClass.Unknown }

sealed class FoldingClass private constructor() {
    class Supported(val foldingFeature: FoldingFeature) : FoldingClass()
    object NotSupported : FoldingClass()
    object Unknown : FoldingClass()
    companion object {
        val log = logging()
    }
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
        return FoldingClass.Unknown

    val feature = foldingFeatureList?.displayFeatures?.firstOrNull { it is FoldingFeature }
        ?.let { it as FoldingFeature }

    return if (feature != null) {
        FoldingClass.log.i { "Folding supported: $feature" }
        FoldingClass.Supported(feature)
    } else {
        FoldingClass.log.i { "Folding not supported" }
        FoldingClass.NotSupported
    }
}
