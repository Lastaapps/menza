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

@file:OptIn(
    ExperimentalDecomposeApi::class,
    ExperimentalDecomposeApi::class,
    ExperimentalDecomposeApi::class,
)

package cz.lastaapps.menza.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.PredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimatable
import com.arkivanov.essenty.backhandler.BackEvent
import com.arkivanov.essenty.backhandler.BackHandler

val appPredictiveBackAnimatable: (
    initialBackEvent: BackEvent,
) -> PredictiveBackAnimatable = {
    com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback
        // .materialPredictiveBackAnimatable(it)
        .androidPredictiveBackAnimatableV1(it)
}

fun appPredictiveBackParams(
    backHandler: BackHandler,
    onBack: () -> Unit,
) = PredictiveBackParams(
    backHandler = backHandler,
    onBack = onBack,
    animatable = appPredictiveBackAnimatable,
)

fun fadingPredictiveBackParams(
    backHandler: BackHandler,
    onBack: () -> Unit,
) = PredictiveBackParams(
    backHandler = backHandler,
    onBack = onBack,
    animatable = {
        @Suppress("UNUSED_ANONYMOUS_PARAMETER")
        predictiveBackAnimatable(
            initialBackEvent = it,
            exitModifier = { progress: Float, edge: BackEvent.SwipeEdge ->
                Modifier
                    .graphicsLayer {
                        alpha = (1f - 2 * progress).coerceAtLeast(0f)
                    }
            },
            enterModifier = { progress: Float, edge: BackEvent.SwipeEdge -> Modifier },
        )
    },
)
