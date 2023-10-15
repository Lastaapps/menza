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

package cz.lastaapps.menza.ui.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bumble.appyx.interactions.core.model.BaseAppyxComponent
import com.bumble.appyx.interactions.core.model.removedElements
import com.bumble.appyx.interactions.core.modifiers.onPointerEvent
import com.bumble.appyx.interactions.core.ui.LocalBoxScope
import com.bumble.appyx.interactions.core.ui.LocalMotionProperties
import com.bumble.appyx.interactions.core.ui.context.TransitionBounds
import com.bumble.appyx.interactions.core.ui.context.UiContext
import com.bumble.appyx.interactions.core.ui.output.ElementUiModel
import com.bumble.appyx.navigation.composable.Child
import com.bumble.appyx.navigation.composable.ChildRenderer
import com.bumble.appyx.navigation.integration.LocalScreenSize
import com.bumble.appyx.navigation.node.ParentNode
import kotlin.math.roundToInt


internal val defaultExtraTouch = 48.dp

/**
 * Stolen from AppyxComponent
 */
@Composable
fun <InteractionTarget : Any, ModelState : Any> ParentNode<InteractionTarget>.AppyxNoDragComponent(
    appyxComponent: BaseAppyxComponent<InteractionTarget, ModelState>,
    modifier: Modifier = Modifier,
    clipToBounds: Boolean = false,
    gestureExtraTouchArea: Dp = defaultExtraTouch,
    block: @Composable (ChildrenTransitionNoDragScope<InteractionTarget, ModelState>.() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val screenWidthPx = (LocalScreenSize.current.widthDp * density.density).value.roundToInt()
    val screenHeightPx = (LocalScreenSize.current.heightDp * density.density).value.roundToInt()
    val coroutineScope = rememberCoroutineScope()
    var containerSize by remember { mutableStateOf(IntSize.Zero) }
    val childrenBlock = block ?: {
        children { child, _ ->
            child()
        }
    }

    SideEffect {
        appyxComponent.updateContext(
            UiContext(
                coroutineScope = coroutineScope,
                clipToBounds = clipToBounds,
            ),
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(if (clipToBounds) Modifier.clipToBounds() else Modifier)
            .onPlaced {
                containerSize = it.size
                appyxComponent.updateBounds(
                    TransitionBounds(
                        density = density,
                        widthPx = it.size.width,
                        heightPx = it.size.height,
                        screenWidthPx = screenWidthPx,
                        screenHeightPx = screenHeightPx,
                    ),
                )
            }
            .onPointerEvent {
                if (it.type == PointerEventType.Release) {
                    appyxComponent.onRelease()
                }
            },
    ) {
        CompositionLocalProvider(LocalBoxScope provides this@Box) {
            childrenBlock(
                ChildrenTransitionNoDragScope(
                    appyxComponent,
                    gestureExtraTouchArea,
                ),
            )
        }
    }
}

class ChildrenTransitionNoDragScope<InteractionTarget : Any, NavState : Any>(
    private val appyxComponent: BaseAppyxComponent<InteractionTarget, NavState>,
    private val gestureExtraTouchArea: Dp,
) {

    @Suppress("ComposableNaming", "LongMethod", "ModifierMissing")
    @Composable
    fun ParentNode<InteractionTarget>.children(
        block: @Composable (child: ChildRenderer, elementUiModel: ElementUiModel<InteractionTarget>) -> Unit,
    ) {

        val saveableStateHolder = rememberSaveableStateHolder()
        val appyxComponent = this@ChildrenTransitionNoDragScope.appyxComponent

        LaunchedEffect(appyxComponent) {
            appyxComponent
                .removedElements()
                .collect { deletedKeys ->
                    deletedKeys.forEach { navKey ->
                        saveableStateHolder.removeState(navKey)
                    }
                }
        }

        val density = LocalDensity.current
        val gestureExtraTouchAreaPx = with(density) { gestureExtraTouchArea.toPx() }
        val uiModels by this@ChildrenTransitionNoDragScope.appyxComponent.uiModels.collectAsState()

        uiModels
            .forEach { elementUiModel ->
                val id = elementUiModel.element.id

                key(id) {
                    var transformedBoundingBox by remember(id) { mutableStateOf(Rect.Zero) }
                    var elementSize by remember(id) { mutableStateOf(IntSize.Zero) }
                    var offsetCenter by remember(id) { mutableStateOf(Offset.Zero) }
                    val isVisible by elementUiModel.visibleState.collectAsState()

                    elementUiModel.persistentContainer()

                    if (isVisible) {
                        CompositionLocalProvider(
                            LocalMotionProperties provides elementUiModel.motionProperties,
                        ) {
                            Child(
                                elementUiModel = elementUiModel.copy(
                                    modifier = Modifier
                                        .then(elementUiModel.modifier)
                                        .onPlaced {
                                            elementSize = it.size
                                            val localCenter = Offset(
                                                it.size.width.toFloat(),
                                                it.size.height.toFloat(),
                                            ) / 2f
                                            transformedBoundingBox =
                                                it
                                                    .boundsInParent()
                                                    .inflate(gestureExtraTouchAreaPx)
                                            offsetCenter =
                                                transformedBoundingBox.center - localCenter
                                        },
                                ),
                                saveableStateHolder = saveableStateHolder,
                                decorator = block,
                            )
                        }
                    }
                }
            }
    }
}