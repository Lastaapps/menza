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

package cz.lastaapps.menza.features.settings.ui.navigation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import cz.lastaapps.menza.features.other.ui.node.DefaultLicenseComponent
import cz.lastaapps.menza.features.other.ui.node.DefaultOsturakComponent
import cz.lastaapps.menza.features.other.ui.node.LicenseComponent
import cz.lastaapps.menza.features.other.ui.node.LicenseContent
import cz.lastaapps.menza.features.other.ui.node.OsturakComponent
import cz.lastaapps.menza.features.other.ui.node.OsturakContent
import cz.lastaapps.menza.features.settings.ui.component.AppThemeComponent
import cz.lastaapps.menza.features.settings.ui.component.AppThemeContent
import cz.lastaapps.menza.features.settings.ui.component.DefaultAppThemeComponent
import cz.lastaapps.menza.features.settings.ui.component.DefaultDishLanguageComponent
import cz.lastaapps.menza.features.settings.ui.component.DefaultSettingsComponent
import cz.lastaapps.menza.features.settings.ui.component.DishLanguageComponent
import cz.lastaapps.menza.features.settings.ui.component.DishLanguageContent
import cz.lastaapps.menza.features.settings.ui.component.SettingsComponent
import cz.lastaapps.menza.features.settings.ui.component.SettingsContent
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent.Config.AppTheme
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent.Config.DishLanguage
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent.Config.License
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent.Config.Osturak
import cz.lastaapps.menza.features.settings.ui.navigation.DefaultSettingsHubComponent.Config.Settings
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsHubComponent.Child
import kotlinx.serialization.Serializable

internal interface SettingsHubComponent : BackHandlerOwner {
    val content: Value<ChildStack<*, Child>>

    fun toChooseTheme()
    fun toChooseDishLanguage()
    fun toOsturak()
    fun toLicense()
    fun pop()

    sealed interface Child {
        @JvmInline
        value class Settings(val component: SettingsComponent) : Child

        @JvmInline
        value class AppTheme(val component: AppThemeComponent) : Child

        @JvmInline
        value class DishLanguage(val component: DishLanguageComponent) : Child

        @JvmInline
        value class Osturak(val component: OsturakComponent) : Child

        @JvmInline
        value class License(val component: LicenseComponent) : Child
    }
}

internal class DefaultSettingsHubComponent(
    componentContext: ComponentContext,
) : SettingsHubComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    override val content: Value<ChildStack<*, Child>> =
        childStack(
            navigation,
            Config.serializer(),
            initialStack = { listOf(Config.Settings) },
        ) { configuration, componentContext ->
            when (configuration) {
                AppTheme -> Child.AppTheme(DefaultAppThemeComponent(componentContext))
                DishLanguage -> Child.DishLanguage(DefaultDishLanguageComponent(componentContext))
                License -> Child.License(DefaultLicenseComponent(componentContext))
                Osturak -> Child.Osturak(DefaultOsturakComponent(componentContext))
                Settings -> Child.Settings(DefaultSettingsComponent(componentContext))
            }
        }

    override fun toChooseTheme() {
        navigation.push(Config.AppTheme)
    }

    override fun toChooseDishLanguage() {
        navigation.push(Config.DishLanguage)
    }

    override fun toOsturak() {
        navigation.push(Config.Osturak)
    }

    override fun toLicense() {
        navigation.push(Config.License)
    }

    override fun pop() {
        navigation.pop()
    }


    @Serializable
    private sealed interface Config {
        @Serializable
        data object Settings : Config

        @Serializable
        data object AppTheme : Config

        @Serializable
        data object DishLanguage : Config

        @Serializable
        data object Osturak : Config

        @Serializable
        data object License : Config
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun SettingsHubContent(
    component: SettingsHubComponent,
    modifier: Modifier = Modifier,
) {
    val stack by component.content.subscribeAsState()
    Children(
        stack = stack,
        animation = predictiveBackAnimation(
            backHandler = component.backHandler,
            fallbackAnimation = stackAnimation(),
            onBack = component::pop,
        ),
    ) {
        Surface {
            when (val instance = it.instance) {
                is Child.AppTheme -> AppThemeContent(
                    instance.component,
                    onDone = component::pop,
                    modifier,
                )

                is Child.DishLanguage -> DishLanguageContent(
                    instance.component,
                    modifier,
                    component::pop,
                )

                is Child.License -> LicenseContent(instance.component, modifier)
                is Child.Osturak -> OsturakContent(instance.component, modifier)
                is Child.Settings -> SettingsContent(
                    instance.component,
                    onChooseTheme = component::toChooseTheme,
                    onChooseDishLanguage = component::toChooseDishLanguage,
                    onOsturak = component::toOsturak,
                    onLicense = component::toLicense,
                    modifier,
                )
            }
        }
    }
}
