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

@file:OptIn(ExperimentalDecomposeApi::class)

package cz.lastaapps.menza.ui.util

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.window.layout.FoldingFeature
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsLayout
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import cz.lastaapps.menza.ui.locals.FoldingClass
import cz.lastaapps.menza.ui.locals.LocalFoldProvider
import cz.lastaapps.menza.ui.locals.isBookPosture
import cz.lastaapps.menza.ui.locals.isTableTopPosture

class ChildPanelsFoldingLayout(
    dualWeights: Pair<Float, Float> = 1F to 1F,
    private val enableTableTop: Boolean = true,
    private val minWeightsTableTop: Pair<Float, Float> = .42F to .42F,
    private val enableBook: Boolean = true,
    private val minWeightsBook: Pair<Float, Float> = .32F to .32F,
) : ChildPanelsLayout {
    private val singleMeasurePolicy = SingleMeasurePolicy()
    private val dualMeasurePolicy = DualMeasurePolicy(weights = dualWeights)

    private data class WindowPosition(
        val positionInWindow: Offset,
        val size: IntSize,
    )

    @Composable
    override fun Layout(
        mode: ChildPanelsMode,
        main: @Composable () -> Unit,
        details: @Composable () -> Unit,
        extra: @Composable () -> Unit,
    ) {
        var layoutCoordinates by remember { mutableStateOf<WindowPosition?>(null) }

        val measurePolicy =
            when (mode) {
                ChildPanelsMode.SINGLE -> singleMeasurePolicy
                ChildPanelsMode.DUAL -> {
                    val foldingFeature =
                        (LocalFoldProvider.current as? FoldingClass.Supported)?.foldingFeature
                    remember(foldingFeature, layoutCoordinates) {
                        layoutCoordinates?.let {
                            when {
                                enableTableTop && foldingFeature.isTableTopPosture() ->
                                    TableTopMeasurePolicy(
                                        foldingFeature,
                                        it,
                                        minWeightsTableTop,
                                    )

                                enableBook && foldingFeature.isBookPosture() ->
                                    BookMeasurePolicy(
                                        foldingFeature,
                                        it,
                                        minWeightsBook,
                                    )

                                else -> null
                            }
                        } ?: dualMeasurePolicy
                    }
                }

                ChildPanelsMode.TRIPLE -> error("Not supported")
            }

        androidx.compose.ui.layout.Layout(
            content = {
                main()
                details()
            },
            modifier =
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        layoutCoordinates =
                            WindowPosition(
                                positionInWindow = it.positionInWindow(),
                                size = it.size,
                            )
                    }.fillMaxSize(),
            measurePolicy = measurePolicy,
        )
    }

    private class SingleMeasurePolicy : MeasurePolicy {
        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureResult {
            val placeables = measurables.map { it.measure(constraints) }

            return layout(constraints.maxWidth, constraints.maxHeight) {
                placeables.forEach {
                    it.placeRelative(x = 0, y = 0)
                }
            }
        }
    }

    private class DualMeasurePolicy(
        weights: Pair<Float, Float>,
    ) : MeasurePolicy {
        private val primaryWeight = weights.first / (weights.first + weights.second)

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureResult {
            val w1 = (constraints.maxWidth.toFloat() * primaryWeight).toInt()
            val w2 = constraints.maxWidth - w1
            val placeable1 = measurables[0].measure(constraints.copy(maxWidth = w1, minWidth = w1))
            val placeable2 = measurables[1].measure(constraints.copy(maxWidth = w2, minWidth = w2))

            return layout(constraints.maxWidth, constraints.maxHeight) {
                placeable1.placeRelative(x = 0, y = 0)
                placeable2.placeRelative(x = w1, y = 0)
            }
        }
    }

    private class TableTopMeasurePolicy(
        foldingFeature: FoldingFeature,
        layoutCoordinates: WindowPosition,
        minWeights: Pair<Float, Float>,
    ) : MeasurePolicy {
        private val topSize =
            (foldingFeature.bounds.top - layoutCoordinates.positionInWindow.y)
                .coerceIn(
                    (minWeights.first * layoutCoordinates.size.height),
                    ((1F - minWeights.second) * layoutCoordinates.size.height),
                ).toInt()
        private val boundsHeight = foldingFeature.bounds.height()
        private val bottomSize = layoutCoordinates.size.height - topSize - boundsHeight

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureResult {
            val placeable1 =
                measurables[0].measure(
                    constraints.copy(
                        maxHeight = bottomSize,
                        minHeight = bottomSize,
                    ),
                )
            val placeable2 =
                measurables[1].measure(
                    constraints.copy(
                        maxHeight = topSize,
                        minHeight = topSize,
                    ),
                )

            return layout(constraints.maxWidth, constraints.maxHeight) {
                placeable1.placeRelative(x = 0, y = topSize + boundsHeight)
                placeable2.placeRelative(x = 0, y = 0)
            }
        }
    }

    private class BookMeasurePolicy(
        foldingFeature: FoldingFeature,
        layoutCoordinates: WindowPosition,
        minWeights: Pair<Float, Float>,
    ) : MeasurePolicy {
        private val leftSize =
            (foldingFeature.bounds.left - layoutCoordinates.positionInWindow.x)
                .coerceIn(
                    (minWeights.first * layoutCoordinates.size.width),
                    ((1F - minWeights.second) * layoutCoordinates.size.width),
                ).toInt()
        private val boundsWidth = foldingFeature.bounds.width()
        private val rightSize = layoutCoordinates.size.width - leftSize - boundsWidth

        override fun MeasureScope.measure(
            measurables: List<Measurable>,
            constraints: Constraints,
        ): MeasureResult {
            val placeable1 =
                measurables[0].measure(constraints.copy(maxWidth = leftSize, minWidth = leftSize))
            val placeable2 =
                measurables[1].measure(constraints.copy(maxWidth = rightSize, minWidth = rightSize))

            return layout(constraints.maxWidth, constraints.maxHeight) {
                placeable1.placeRelative(x = 0, y = 0)
                placeable2.placeRelative(x = leftSize + boundsWidth, y = 0)
            }
        }
    }
}

@Composable
fun rememberChildPanelsFoldingLayout(): ChildPanelsLayout =
    remember {
        ChildPanelsFoldingLayout()
    }
