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

package cz.lastaapps.menza.features.main.ui.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushToFront
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import cz.lastaapps.menza.features.info.ui.component.DefaultInfoComponent
import cz.lastaapps.menza.features.info.ui.component.InfoComponent
import cz.lastaapps.menza.features.main.ui.component.DefaultDrawerComponent
import cz.lastaapps.menza.features.main.ui.component.DrawerComponent
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import cz.lastaapps.menza.features.other.ui.node.DefaultLicenseComponent
import cz.lastaapps.menza.features.other.ui.node.DefaultOsturakComponent
import cz.lastaapps.menza.features.other.ui.node.LicenseComponent
import cz.lastaapps.menza.features.other.ui.node.OsturakComponent
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsHubComponent
import cz.lastaapps.menza.features.starting.ui.component.DefaultPolicyComponent
import cz.lastaapps.menza.features.starting.ui.component.PolicyComponent
import cz.lastaapps.menza.features.today.ui.navigation.DefaultTodayComponent
import cz.lastaapps.menza.features.today.ui.navigation.TodayComponent
import cz.lastaapps.menza.features.week.ui.node.DefaultWeekComponent
import cz.lastaapps.menza.features.week.ui.node.WeekComponent
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

internal interface MainComponent : BackHandlerOwner {
    val viewModel: MainViewModel
    val content: Value<ChildStack<*, Child>>

    val drawerComponent: DrawerComponent

    fun push(target: MainNavTarget)

    fun pushRoot(target: MainNavTarget)

    fun pop()

    sealed interface Child {
        @JvmInline
        value class Today(val component: TodayComponent) : Child

        @JvmInline
        value class Week(val component: WeekComponent) : Child

        @JvmInline
        value class Info(val component: InfoComponent) : Child

        @JvmInline
        value class Settings(val component: SettingsHubComponent) : Child

        @JvmInline
        value class Osturak(val component: OsturakComponent) : Child

        @JvmInline
        value class PrivacyPolicy(val component: PolicyComponent) : Child

        @JvmInline
        value class LicenseNotices(val component: LicenseComponent) : Child
    }
}

internal class DefaultMainComponent(
    componentContext: ComponentContext,
) : MainComponent, KoinComponent, ComponentContext by componentContext {
    override val viewModel: MainViewModel = getOrCreateKoin()

    private val navigation = StackNavigation<Config>()
    override val content: Value<ChildStack<*, Child>> =
        childStack(
            navigation,
            Config.serializer(),
            initialStack = { listOf(Config.Today) },
        ) { configuration, componentContext ->
            when (configuration) {
                Config.Info -> Child.Info(DefaultInfoComponent(componentContext))
                Config.LicenseNotices -> Child.LicenseNotices(
                    DefaultLicenseComponent(
                        componentContext,
                    ),
                )

                Config.Osturak -> Child.Osturak(DefaultOsturakComponent(componentContext))
                Config.PrivacyPolicy -> Child.PrivacyPolicy(
                    DefaultPolicyComponent(componentContext, false),
                )

                Config.Settings -> Child.Settings(DefaultSettingsHubComponent(componentContext))
                Config.Today -> Child.Today(DefaultTodayComponent(componentContext))
                Config.Week -> Child.Week(DefaultWeekComponent(componentContext))
            }
        }

    override val drawerComponent: DrawerComponent =
        DefaultDrawerComponent(childContext("drawer"))

    @OptIn(ExperimentalDecomposeApi::class)
    override fun push(target: MainNavTarget) {
        navigation.pushToFront(Config.fromTarget(target))
    }

    override fun pushRoot(target: MainNavTarget) {
        val config = Config.fromTarget(target)

        navigation.navigate { _ ->
            // TodayNav is always at the bottom
            listOfNotNull(
                Config.Today,
                config.takeUnless { it == Config.Today },
            )
        }
    }

    override fun pop() {
        navigation.pop()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data object Today : Config

        @Serializable
        data object Week : Config

        @Serializable
        data object Info : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object Osturak : Config

        @Serializable
        data object PrivacyPolicy : Config

        @Serializable
        data object LicenseNotices : Config

        companion object {
            fun fromTarget(target: MainNavTarget) =
                when (target) {
                    MainNavTarget.Info -> Info
                    MainNavTarget.LicenseNotices -> LicenseNotices
                    MainNavTarget.Osturak -> Osturak
                    MainNavTarget.PrivacyPolicy -> PrivacyPolicy
                    MainNavTarget.Settings -> Settings
                    MainNavTarget.Today -> Today
                    MainNavTarget.Week -> Week
                }
        }
    }
}
