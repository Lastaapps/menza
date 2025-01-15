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

package cz.lastaapps.menza.features.today.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.value.Value
import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.menza.features.today.ui.navigation.DefaultDishDetailComponent.Config.Rate
import cz.lastaapps.menza.features.today.ui.navigation.DishDetailComponent.Child
import cz.lastaapps.menza.features.today.ui.screen.DishDetailScreen
import cz.lastaapps.menza.features.today.ui.vm.DishDetailViewModel
import cz.lastaapps.menza.ui.util.AnimationScopes
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf

internal interface DishDetailComponent {
    val viewModel: DishDetailViewModel

    val dialogContent: Value<ChildSlot<*, Child>>

    fun onRateDish(dish: DishOriginDescriptor)

    sealed interface Child {
        @JvmInline
        value class Rate(
            val component: RateDishComponent,
        ) : Child
    }
}

internal class DefaultDishDetailComponent(
    componentContext: ComponentContext,
    dishOrigin: DishOriginDescriptor,
    dishInitial: Dish?,
) : DishDetailComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: DishDetailViewModel =
        getOrCreateKoin { parametersOf(dishOrigin, dishInitial) }

    private val navigation = SlotNavigation<Config>()
    override val dialogContent: Value<ChildSlot<*, Child>> =
        childSlot(
            navigation,
            Config.serializer(),
            initialConfiguration = { null },
        ) { configuration, componentContext ->
            when (configuration) {
                is Rate ->
                    Child.Rate(
                        DefaultRateDishComponent(
                            componentContext,
                            configuration.dish,
                            navigation::dismiss,
                        ),
                    )
            }
        }

    override fun onRateDish(dish: DishOriginDescriptor) {
        navigation.activate(Rate(dish))
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data class Rate(
            val dish: DishOriginDescriptor,
        ) : Config
    }
}

@Composable
internal fun DishDetailContent(
    component: DishDetailComponent,
    scopes: AnimationScopes,
    modifier: Modifier = Modifier,
) {
    DishDetailScreen(
        component.viewModel,
        onRating = component::onRateDish,
        scopes = scopes,
        modifier = modifier,
    )

    // Dialogs
    val slot by component.dialogContent.subscribeAsState()
    when (val instance = slot.child?.instance) {
        is Child.Rate -> RateDishContent(instance.component)
        null -> {}
    }
}
