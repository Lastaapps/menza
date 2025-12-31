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

package cz.lastaapps.menza.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Elevation
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.IndicatorMaxDistance
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.indicatorShape
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshWrapper(
    isRefreshing: Boolean,
    useAIMode: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    // move to the call side after it is not experimental any more
    val state: PullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier
            .pullToRefresh(
                state = state,
                isRefreshing = isRefreshing,
                onRefresh = onRefresh,
            ).onKeyEvent {
                if ((it.isCtrlPressed && it.key == Key.R) || it.key == Key.Refresh) {
                    onRefresh()
                    return@onKeyEvent true
                }
                false
            }.clipToBounds(),
    ) {
        content()

        if (enabled) {
            val scaleFraction = {
                if (isRefreshing) {
                    1f
                } else {
                    LinearOutSlowInEasing.transform(state.distanceFraction).coerceIn(0f, 1f)
                }
            }

            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        scaleX = scaleFraction()
                        scaleY = scaleFraction()
                    },
            ) {
                if (!useAIMode) {
                    PullToRefreshDefaults.Indicator(state = state, isRefreshing = isRefreshing)
                } else {
                    AIIndicator(state, isRefreshing)
                }
            }
        }
    }
}

@Composable
private fun AIIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    containerColor: Color = PullToRefreshDefaults.indicatorContainerColor,
    color: Color = PullToRefreshDefaults.indicatorColor,
    maxDistance: Dp = IndicatorMaxDistance,
) {
    IndicatorBox(
        modifier = modifier,
        state = state,
        shape = CircleShape,
        isRefreshing = isRefreshing,
        containerColor = containerColor,
        maxDistance = maxDistance * 0.8f,
    ) {
        Crossfade(
            targetState = isRefreshing,
            label = "Refresh state",
        ) { refreshing ->
            val textModifier: Modifier = Modifier.textFading(state, refreshing)
            val iconModifier: Modifier = Modifier.iconRotation(state)

            Row(
                modifier =
                    textModifier.padding(
                        vertical = Padding.Small,
                        horizontal = Padding.Medium,
                    ),
                horizontalArrangement = Arrangement.spacedBy(Padding.Smaller),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Default.Psychology,
                    null,
                    iconModifier.graphicsLayer { scaleX = -1f },
                )
                Text(
                    stringResource(R.string.ui_pull_to_refresh_thinking),
                    style = MaterialTheme.typography.bodyMedium,
                    color = color,
                    maxLines = 1,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}

@Composable
private fun Modifier.textFading(
    state: PullToRefreshState,
    refreshing: Boolean,
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    if (refreshing) {
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(800, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "alpha",
        )
        return this.graphicsLayer { this.alpha = 1f - alpha }
    } else {
        return this.graphicsLayer {
            this.alpha = FastOutLinearInEasing.transform(state.distanceFraction.coerceIn(0f, 1f))
        }
    }
}

@Composable
private fun Modifier.iconRotation(
    state: PullToRefreshState,
    chanceForSkipOneIn: Int = 20,
): Modifier {
    val allowIconRotation = remember(state.isAnimating) { Random.nextInt(chanceForSkipOneIn) == 0 }
    if (!allowIconRotation) {
        return this
    }
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_pulse")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(650, easing = FastOutLinearInEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "rotation",
    )
    return this.graphicsLayer {
        rotationZ = rotation * 360f
        scaleX = 2 * (1f - rotation) - 1f
        scaleY = 2 * ((1f - rotation) * 2f).mod(1f) - 1f
    }
}

/**
 * Stolen from the PullToRefresh.kt from compose
 * Removed the size constraint
 */
@Composable
private fun IndicatorBox(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    maxDistance: Dp = IndicatorMaxDistance,
    shape: Shape = indicatorShape,
    containerColor: Color = Color.Unspecified,
    elevation: Dp = Elevation,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier =
            modifier
                .drawWithContent {
                    clipRect(
                        top = 0f,
                        left = -Float.MAX_VALUE,
                        right = Float.MAX_VALUE,
                        bottom = Float.MAX_VALUE,
                    ) {
                        this@drawWithContent.drawContent()
                    }
                }.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        placeable.placeWithLayer(
                            0,
                            0,
                            layerBlock = {
                                val showElevation = state.distanceFraction > 0f || isRefreshing
                                translationY =
                                    state.distanceFraction * maxDistance.roundToPx() -
                                    size.height
                                shadowElevation = if (showElevation) elevation.toPx() else 0f
                                this.shape = shape
                                clip = true
                            },
                        )
                    }
                }.background(color = containerColor, shape = shape),
        contentAlignment = Alignment.Center,
        content = content,
    )
}
