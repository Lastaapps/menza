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

@file:OptIn(ExperimentalDecomposeApi::class, ExperimentalSerializationApi::class)

package cz.lastaapps.menza.features.today.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanels
import com.arkivanov.decompose.extensions.compose.experimental.panels.ChildPanelsAnimators
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.PredictiveBackParams
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.experimental.stack.animation.scale
import com.arkivanov.decompose.router.panels.ChildPanels
import com.arkivanov.decompose.router.panels.ChildPanelsMode
import com.arkivanov.decompose.router.panels.Panels
import com.arkivanov.decompose.router.panels.PanelsNavigation
import com.arkivanov.decompose.router.panels.activateDetails
import com.arkivanov.decompose.router.panels.childPanels
import com.arkivanov.decompose.router.panels.dismissDetails
import com.arkivanov.decompose.router.panels.setMode
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.backhandler.BackHandlerOwner
import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.toOrigin
import cz.lastaapps.menza.features.today.ui.navigation.DefaultTodayComponent.Config.DetailsConfig
import cz.lastaapps.menza.features.today.ui.vm.TodayViewModel
import cz.lastaapps.menza.features.today.ui.widget.NoDishSelected
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.ChildPanelsModeEffect
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.serializer
import org.koin.core.component.KoinComponent

internal interface TodayComponent : BackHandlerOwner {
    val viewModel: TodayViewModel

    val content: Value<ChildPanels<*, DishListComponent, *, DishDetailComponent, Nothing, Nothing>>

    fun dismissDetail()

    fun setPanelMode(mode: ChildPanelsMode)

    fun onBackClicked()

//    sealed interface Child {
//        @JvmInline
//        value class DishList(
//            val component: DishListComponent,
//        ) : Child
//
//        @JvmInline
//        value class Detail(
//            val component: DetailComponent,
//        ) : Child
//    }
}

internal class DefaultTodayComponent(
    componentContext: ComponentContext,
) : TodayComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: TodayViewModel = getOrCreateKoin()

    private val navigation = PanelsNavigation<Unit, Config, Nothing>()
    override val content: Value<ChildPanels<*, DishListComponent, *, DishDetailComponent, Nothing, Nothing>> =
        childPanels(
            source = navigation,
            serializers = Unit.serializer() to Config.serializer(),
            initialPanels = { Panels(main = Unit) },
            handleBackButton = true,
            mainFactory = { _, ctx ->
                DefaultDishListComponent(
                    ctx,
                    onDishSelected = { dish ->
                        navigation.activateDetails(
                            DetailsConfig(dish.toOrigin(), dish),
                        )
                    },
                )
            },
            detailsFactory = { cfg, ctx ->
                when (cfg) {
                    is DetailsConfig -> {
                        DefaultDishDetailComponent(
                            componentContext = ctx,
                            dishOrigin = cfg.dishDescriptor,
                            dishInitial = cfg.initialDish,
                        )
                    }
                }
            },
        )

    override fun setPanelMode(mode: ChildPanelsMode) {
        navigation.setMode(mode)
    }

    override fun dismissDetail() {
        navigation.dismissDetails()
    }

    override fun onBackClicked() {
        navigation.dismissDetails()
    }

    @Serializable
    private sealed interface Config {
        @Serializable
        data class DetailsConfig(
            val dishDescriptor: DishOriginDescriptor,
            // does not serialize the field
            @Transient
            val initialDish: Dish? = null,
        ) : Config
    }
}

@Composable
internal fun TodayContent(
    component: TodayComponent,
    onOsturak: () -> Unit,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val state by component.viewModel.flowState
    LaunchedEffect(state.selectedMenza) {
        state.selectedMenza?.let {
            component.dismissDetail()
        }
    }

    ChildPanelsModeEffect(component::setPanelMode)

    val panelModifier =
        Modifier
            .fillMaxSize()
            .padding(horizontal = Padding.More.Screen)
    ChildPanels(
        modifier = modifier.padding(vertical = Padding.More.Screen),
        panels = component.content,
        mainChild = {
            DishListContent(
                it.instance,
                onOsturak = onOsturak,
                hostState = hostState,
                modifier = panelModifier,
            )
        },
        detailsChild = {
            DishDetailContent(
                it.instance,
                modifier = panelModifier,
            )
        },
        secondPanelPlaceholder = {
            NoDishSelected(
                modifier = panelModifier,
            )
        },
        animators =
            ChildPanelsAnimators(
                single = fade() + scale(),
                dual = fade() to fade(),
            ),
        predictiveBackParams = {
            PredictiveBackParams(
                backHandler = component.backHandler,
                onBack = component::onBackClicked,
            )
        },
    )
}
