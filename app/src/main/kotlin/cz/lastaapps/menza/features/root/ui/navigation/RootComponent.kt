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

package cz.lastaapps.menza.features.root.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import cz.lastaapps.menza.features.root.ui.RootViewModel
import cz.lastaapps.menza.features.root.ui.navigation.DefaultRootComponent.Config.AppContentConfig
import cz.lastaapps.menza.features.root.ui.navigation.DefaultRootComponent.Config.AppSetupConfig
import cz.lastaapps.menza.features.root.ui.navigation.RootComponent.Child
import cz.lastaapps.menza.features.root.ui.navigation.RootComponent.Child.AppContent
import cz.lastaapps.menza.features.root.ui.navigation.RootComponent.Child.AppSetup
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent


// TODO delete
interface AppContentComponent

internal interface RootComponent {
    val viewModel: RootViewModel
    val content: Value<ChildSlot<*, Child>>

    fun toInitialSetup()
    fun toAppContent()

    sealed interface Child {
        @JvmInline
        value class AppContent(val component: AppContentComponent) : Child

        @JvmInline
        value class AppSetup(val component: StartingComponent) : Child
    }
}

internal class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    override val viewModel = getOrCreateKoin<RootViewModel>()

    private val navigation = SlotNavigation<Config>()
    override val content: Value<ChildSlot<*, Child>> =
        childSlot(
            navigation,
            Config.serializer(),
        ) { config, componentContext ->
            when (config) {
                AppContentConfig -> AppContent(object : AppContentComponent {})
                AppSetupConfig -> AppSetup(DefaultStartingComponent(componentContext))
            }
        }

    override fun toInitialSetup() {
        navigation.activate(AppSetupConfig)
    }

    override fun toAppContent() {
        navigation.activate(AppContentConfig)
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object AppSetupConfig : Config

        @Serializable
        data object AppContentConfig : Config
    }
}
