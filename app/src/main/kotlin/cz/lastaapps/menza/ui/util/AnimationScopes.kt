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

@file:OptIn(ExperimentalSharedTransitionApi::class, ExperimentalSharedTransitionApi::class)

package cz.lastaapps.menza.ui.util

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize
import androidx.compose.animation.SharedTransitionScope.PlaceHolderSize.Companion.contentSize
import androidx.compose.animation.SharedTransitionScope.ResizeMode
import androidx.compose.animation.SharedTransitionScope.ResizeMode.Companion.ScaleToBounds
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

data class AnimationScopes(
    val sharedTransitionScope: SharedTransitionScope,
    val animatedVisibilityScope: AnimatedVisibilityScope,
    val isEnabled: Boolean,
)

context(AnimatedVisibilityScope)
fun SharedTransitionScope.AnimationScopes(isEnabled: Boolean = true) =
    AnimationScopes(this, this@AnimatedVisibilityScope, isEnabled = isEnabled)

@Composable
fun Modifier.sharedBounds(
    scopes: AnimationScopes,
    key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Center),
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier =
    with(scopes.sharedTransitionScope) {
        if (!scopes.isEnabled) {
            return@with this@sharedBounds
        }

        this@sharedBounds
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key),
                animatedVisibilityScope = scopes.animatedVisibilityScope,
                enter = enter,
                exit = exit,
                resizeMode = resizeMode,
                placeHolderSize = placeHolderSize,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
            )
    }

@Composable
fun Modifier.sharedContainer(
    scopes: AnimationScopes,
    key: Any,
    clipInOverlayDuringTransitionShape: Shape,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Center),
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier =
    sharedContainer(
        scopes,
        key,
        enter,
        exit,
        resizeMode,
        placeHolderSize,
        renderInOverlayDuringTransition,
        zIndexInOverlay,
        OverlayParentClip(clipInOverlayDuringTransitionShape),
    )

@Composable
fun Modifier.sharedContainer(
    scopes: AnimationScopes,
    key: Any,
    enter: EnterTransition = fadeIn(),
    exit: ExitTransition = fadeOut(),
    resizeMode: ResizeMode = ScaleToBounds(ContentScale.FillWidth, Center),
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = OverlayParentClip(),
): Modifier =
    with(scopes.sharedTransitionScope) {
        if (!scopes.isEnabled) {
            return@with this@sharedContainer
        }

        this@sharedContainer
            .sharedBounds(
                sharedContentState = rememberSharedContentState(key),
                animatedVisibilityScope = scopes.animatedVisibilityScope,
                enter = enter,
                exit = exit,
                resizeMode = resizeMode,
                placeHolderSize = placeHolderSize,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
                clipInOverlayDuringTransition = clipInOverlayDuringTransition,
            )
    }

@Composable
fun Modifier.sharedElement(
    scopes: AnimationScopes,
    key: Any,
    placeHolderSize: PlaceHolderSize = contentSize,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
): Modifier =
    with(scopes.sharedTransitionScope) {
        if (!scopes.isEnabled) {
            return@with this@sharedElement
        }

        this@sharedElement.sharedElement(
            sharedContentState = rememberSharedContentState(key),
            animatedVisibilityScope = scopes.animatedVisibilityScope,
            placeHolderSize = placeHolderSize,
            renderInOverlayDuringTransition = renderInOverlayDuringTransition,
            zIndexInOverlay = zIndexInOverlay,
        )
    }

fun Modifier.renderInSharedTransitionScopeOverlay(
    scopes: AnimationScopes,
    renderInOverlay: () -> Boolean = { scopes.sharedTransitionScope.isTransitionActive },
    zIndexInOverlay: Float = 0f,
): Modifier =
    with(scopes.sharedTransitionScope) {
        if (!scopes.isEnabled) {
            return@with this@renderInSharedTransitionScopeOverlay
        }

        this@renderInSharedTransitionScopeOverlay.renderInSharedTransitionScopeOverlay(
            renderInOverlay = renderInOverlay,
            zIndexInOverlay = zIndexInOverlay,
        )
    }

fun Modifier.skipToLookaheadSize(scopes: AnimationScopes): Modifier =
    with(scopes.sharedTransitionScope) {
        if (!scopes.isEnabled) {
            return@with this@skipToLookaheadSize
        }

        this@skipToLookaheadSize.skipToLookaheadSize()
    }

fun OverlayParentClip(roundedCorner: Dp = 0.dp): OverlayClip = OverlayParentClip(RoundedCornerShape(roundedCorner))

fun OverlayParentClip(shape: Shape): OverlayClip =
    object : OverlayClip {
        private val shapedPath = Path()

        override fun getClipPath(
            sharedContentState: SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Path {
            val parentPath = sharedContentState.parentSharedContentState?.clipPathInOverlay

            shapedPath.reset()
            shapedPath.addOutline(
                shape.createOutline(
                    bounds.size,
                    layoutDirection,
                    density,
                ),
            )
            shapedPath.translate(bounds.topLeft)

            // return shapedPath
            // println("--- Me - ${state.key}, Parent - ${state.parentSharedContentState?.key} ---")
            // println("Parent path: ${parentPath?.getBounds()?.let { it.top to it.bottom }}")
            // println("Parent path: ${parentPath?.iterator()?.asSequence()?.toList()}")
            // println("Shaped path: ${shapedPath.getBounds().let { it.top to it.bottom }}")
            // println("Shaped path: ${shapedPath.iterator().asSequence().toList()}")

            return (parentPath?.and(shapedPath) ?: shapedPath)
            // .also { println("Result path: ${it.getBounds().let { it.top to it.bottom }}") }
        }
    }
