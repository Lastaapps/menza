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

package cz.lastaapps.menza.features.starting.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.ChildPages
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.selectNext
import com.arkivanov.decompose.value.Value
import cz.lastaapps.menza.features.other.ui.vm.PolicyViewModel
import cz.lastaapps.menza.features.starting.ui.component.AllSetComponent
import cz.lastaapps.menza.features.starting.ui.component.DefaultAllSetComponent
import cz.lastaapps.menza.features.starting.ui.component.DefaultDownloadComponent
import cz.lastaapps.menza.features.starting.ui.component.DefaultPolicyComponent
import cz.lastaapps.menza.features.starting.ui.component.DefaultPriceTypeComponent
import cz.lastaapps.menza.features.starting.ui.component.DownloadComponent
import cz.lastaapps.menza.features.starting.ui.component.PolicyComponent
import cz.lastaapps.menza.features.starting.ui.component.PriceTypeComponent
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.AllSet
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.ChoosePrice
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.ChooseTheme
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.DownloadData
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.OrderMenzaList
import cz.lastaapps.menza.features.starting.ui.navigation.DefaultStartingComponent.Config.Policy
import cz.lastaapps.menza.features.starting.ui.navigation.StartingComponent.Child
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent


@OptIn(ExperimentalDecomposeApi::class)
internal interface StartingComponent {

    val content: Value<ChildPages<*, Child>>
    val policyViewModel: PolicyViewModel

    fun next()

    sealed interface Child {
        @JvmInline
        value class Policy(val component: PolicyComponent) : Child

        @JvmInline
        value class DownloadData(val component: DownloadComponent) : Child

        @JvmInline
        value class ChoosePrice(val component: PriceTypeComponent) : Child

        @JvmInline
        value class ChooseTheme(val component: ComponentContext) : Child

        @JvmInline
        value class OrderMenzaList(val component: ComponentContext) : Child

        @JvmInline
        value class AllSet(val component: AllSetComponent) : Child
    }
}

@OptIn(ExperimentalDecomposeApi::class)
internal class DefaultStartingComponent(
    componentContext: ComponentContext,
) : StartingComponent, KoinComponent, ComponentContext by componentContext {

    override val policyViewModel: PolicyViewModel = getOrCreateKoin()
    private val navigation = PagesNavigation<Config>()

    override val content: Value<ChildPages<*, Child>> =
        childPages(
            navigation,
            Config.serializer(),
            initialPages = {
                Pages(
                    items = Config.allConfigs,
                    selectedIndex = 0,
                )
            },
        ) { configuration, componentContext ->
            when (configuration) {
                AllSet -> Child.AllSet(DefaultAllSetComponent(componentContext))
                ChoosePrice -> Child.ChoosePrice(DefaultPriceTypeComponent(componentContext))
                ChooseTheme -> TODO()
                DownloadData -> Child.DownloadData(DefaultDownloadComponent(componentContext))
                OrderMenzaList -> TODO()
                Policy -> Child.Policy(DefaultPolicyComponent(componentContext))
            }
        }

    override fun next() {
        navigation.selectNext()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Policy : Config

        @Serializable
        data object DownloadData : Config

        @Serializable
        data object ChoosePrice : Config

        @Serializable
        data object ChooseTheme : Config

        @Serializable
        data object OrderMenzaList : Config

        @Serializable
        data object AllSet : Config

        companion object {
            val allConfigs = listOf(
                Policy,
                DownloadData,
                ChooseTheme,
                ChoosePrice,
                OrderMenzaList,
                AllSet,
            )
        }
    }
}
