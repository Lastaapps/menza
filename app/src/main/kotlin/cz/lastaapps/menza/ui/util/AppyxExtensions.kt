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

import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.operation.activate
import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.model.transition.TransitionModel
import kotlin.math.roundToInt
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


// Spotlight
@Suppress("UNCHECKED_CAST", "KotlinConstantConditions")
fun <Interaction : Any> SpotlightModel<Interaction>.interactionState() =
    (this as TransitionModel<Interaction, SpotlightModel.State<Interaction>>).interactionState()

fun <Interaction : Any> SpotlightModel<Interaction>.items(): Flow<ImmutableList<Interaction>> =
    interactionState().map {
        it.availableElements()
            .map<Element<Interaction>, Interaction> { it.interactionTarget }
            .toPersistentList()
    }

fun <T : Any> SpotlightModel<T>.activeIndex(): Flow<Int> =
    interactionState().map { it.activeIndex.roundToInt() }

fun <Interaction : Any> SpotlightModel<Interaction>.indexOfType(navType: Interaction) =
    items().map { it.indexOf(navType) }

suspend fun <Interaction : Any> Spotlight<Interaction>.activateItem(
    model: SpotlightModel<Interaction>,
    navType: Interaction,
) {
    val index = model.indexOfType(navType).first()
    activate(index.toFloat())
}

// BackStack
@Suppress("UNCHECKED_CAST", "KotlinConstantConditions")
fun <Interaction : Any> BackStack<Interaction>.interactionState(): Flow<BackStackModel.State<Interaction>> =
    (model as TransitionModel<Interaction, BackStackModel.State<Interaction>>).interactionState()

fun <Interaction : Any> BackStack<Interaction>.items(): Flow<ImmutableList<Interaction>> =
    interactionState().map {
        with(model) { it.availableElements() }
            .map<Element<Interaction>, Interaction> { it.interactionTarget }
            .toPersistentList()
    }

fun <Interaction : Any> BackStack<Interaction>.active(): Flow<Interaction> =
    interactionState().map { it.active.interactionTarget }

// Common
fun <Interaction, Model> TransitionModel<Interaction, Model>.interactionState(): Flow<Model> =
    output.map { it.currentTargetState }
