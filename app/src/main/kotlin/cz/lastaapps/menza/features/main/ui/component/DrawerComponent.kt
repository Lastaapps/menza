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

package cz.lastaapps.menza.features.main.ui.component

import androidx.compose.material3.DrawerState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.stack.ChildStack
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import cz.lastaapps.menza.features.main.ui.component.DrawerComponent.Child
import cz.lastaapps.menza.features.settings.ui.component.DefaultReorderMenzaComponent
import cz.lastaapps.menza.features.settings.ui.component.ReorderMenzaComponent
import cz.lastaapps.menza.features.settings.ui.component.ReorderMenzaContent
import cz.lastaapps.menza.ui.theme.appPredictiveBackParams
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

internal interface DrawerComponent : BackHandlerOwner {
    val content: Value<ChildStack<*, Child>>

    fun edit()

    fun pop()

    sealed interface Child {
        @JvmInline
        value class MenzaSelection(
            val component: MenzaSelectionComponent,
        ) : Child

        @JvmInline
        value class Edit(
            val component: ReorderMenzaComponent,
        ) : Child
    }
}

internal class DefaultDrawerComponent(
    componentContext: ComponentContext,
) : DrawerComponent,
    KoinComponent,
    ComponentContext by componentContext {
    private val navigation = StackNavigation<Config>()
    override val content: Value<ChildStack<*, Child>> =
        childStack(
            navigation,
            Config.serializer(),
            initialStack = { listOf(Config.MenzaList) },
            handleBackButton = true,
        ) { configuration, componentContext ->
            when (configuration) {
                Config.MenzaList ->
                    Child.MenzaSelection(
                        DefaultMenzaSelectionComponent(
                            componentContext,
                        ),
                    )

                Config.Edit -> Child.Edit(DefaultReorderMenzaComponent(componentContext))
            }
        }

    override fun edit() {
        navigation.pushNew(Config.Edit)
    }

    override fun pop() {
        navigation.pop()
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object MenzaList : Config

        @Serializable
        data object Edit : Config
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun DrawerContent(
    component: DrawerComponent,
    drawerState: DrawerState,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val content by component.content.subscribeAsState()
    ChildStack(
        modifier = modifier,
        stack = content,
        animation =
            stackAnimation(
                fade() + scale(),
                predictiveBackParams = {
                    appPredictiveBackParams(
                        backHandler = component.backHandler,
                        onBack = component::pop,
                    )
                },
            ),
    ) { item ->
        Surface {
            when (val instance = item.instance) {
                is Child.MenzaSelection ->
                    MenzaSelectionContent(
                        component = instance.component,
                        onEdit = component::edit,
                        drawerState = drawerState,
                        snackbarHostState = snackbarHostState,
                    )

                is Child.Edit ->
                    ReorderMenzaContent(
                        component = instance.component,
                        onComplete = component::pop,
                    )
            }
        }
    }
}
