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

package cz.lastaapps.menza.features.root.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onPlaced
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.value.Value
import com.bumble.appyx.components.spotlight.Spotlight
import com.bumble.appyx.components.spotlight.SpotlightModel
import com.bumble.appyx.components.spotlight.ui.fader.SpotlightFader
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.navigation.node.node
import cz.lastaapps.menza.features.main.ui.navigation.MainNode
import cz.lastaapps.menza.features.root.ui.ChildConfig.AppContentConfig
import cz.lastaapps.menza.features.root.ui.ChildConfig.AppSetupConfig
import cz.lastaapps.menza.features.root.ui.RootComponentChild.AppContent
import cz.lastaapps.menza.features.root.ui.RootComponentChild.AppSetup
import cz.lastaapps.menza.features.root.ui.RootNavType.LoadingNav
import cz.lastaapps.menza.features.root.ui.RootNavType.MainNav
import cz.lastaapps.menza.features.root.ui.RootNavType.SetupFlowNav
import cz.lastaapps.menza.features.starting.ui.navigation.StartingNode
import cz.lastaapps.menza.ui.util.AppyxNoDragComponent
import cz.lastaapps.menza.ui.util.activateItem
import cz.lastaapps.menza.ui.util.activeIndex
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import cz.lastaapps.menza.ui.util.indexOfType
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent

interface AppContentComponent

interface AppSetupComponent

internal sealed interface RootComponentChild {
    @JvmInline
    value class AppSetup(val component: AppSetupComponent) : RootComponentChild

    @JvmInline
    value class AppContent(val component: AppContentComponent) : RootComponentChild
}

@Serializable
private sealed interface ChildConfig {
    @Serializable
    data object AppSetupConfig : ChildConfig

    @Serializable
    data object AppContentConfig : ChildConfig
}

internal interface RootComponent {
    val viewModel: RootViewModel
    val content: Value<ChildSlot<*, RootComponentChild>>

    fun toInitialSetup()
    fun toAppContent()
}

internal class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, KoinComponent, ComponentContext by componentContext {

    override val viewModel = getOrCreateKoin<RootViewModel>()

    private val navigation = SlotNavigation<ChildConfig>()
    override val content: Value<ChildSlot<*, RootComponentChild>> =
        childSlot(
            navigation,
            ChildConfig.serializer(),
            handleBackButton = true,
        ) { config, childComponentContext ->
            when (config) {
                AppContentConfig -> RootComponentChild.AppContent(object : AppContentComponent {})
                AppSetupConfig -> RootComponentChild.AppSetup(object : AppSetupComponent {})
            }
        }

    override fun toInitialSetup() {
        navigation.activate(AppSetupConfig)
    }

    override fun toAppContent() {
        navigation.activate(AppContentConfig)
    }

}

@Composable
internal fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier,
    onReady: () -> Unit,
) {
    val viewModel: RootViewModel = component.viewModel
    val state by viewModel.flowState

    LaunchedEffect(state.isReady, state.isSetUp) {
        if (state.isReady) {
            if (state.isSetUp) {
                component.toAppContent()
            } else {
                component.toInitialSetup()
            }
            onReady()
        }
    }

    val slot by component.content.subscribeAsState()
    AnimatedContent(
        targetState = slot.child?.instance,
        label = "Root slot",
    ) { instance ->
        when (instance) {
            is AppContent -> Text(text = "App content", modifier)
            is AppSetup -> Text(text = "App setup", modifier)
            null -> Spacer(modifier = modifier)
        }
    }
}

internal class RootNode(
    buildContext: BuildContext,
    private val spotlightModel: SpotlightModel<RootNavType> = SpotlightModel(
        RootNavType.types,
        savedStateMap = buildContext.savedStateMap,
    ),
    private val spotlight: Spotlight<RootNavType> = Spotlight(
        model = spotlightModel,
        visualisation = { SpotlightFader(it) },
    ),
    val viewModel: RootViewModel,
    private val onDecided: () -> Unit,
) : ParentNode<RootNavType>(
    buildContext = buildContext,
    appyxComponent = spotlight,
) {
    override fun resolve(interactionTarget: RootNavType, buildContext: BuildContext): Node {
        return when (interactionTarget) {
            LoadingNav -> node(buildContext) {} // Splash screen will be shown
            SetupFlowNav -> StartingNode(
                buildContext,
                {
                    lifecycleScope.launch {
                        spotlight.activateItem(spotlightModel, MainNav)
                    }
                },
            )

            MainNav -> MainNode(buildContext)
        }
    }

    @Composable
    override fun View(modifier: Modifier) {

        val state by viewModel.flowState

        LaunchedEffect(state.isReady, state.isSetUp) {
            if (state.isReady) {
                (if (state.isSetUp) MainNav else SetupFlowNav)
                    .let { spotlight.activateItem(spotlightModel, it) }
            }
        }

        val activeIndex by remember { spotlightModel.activeIndex() }
            .collectAsStateWithLifecycle(-1)
        val indexOfType by remember { spotlightModel.indexOfType(LoadingNav) }
            .collectAsStateWithLifecycle(0)

        AppyxNoDragComponent(
            appyxComponent = spotlight,
            modifier = modifier.onPlaced {
                if (indexOfType != activeIndex) {
                    onDecided()
                }
            },
        )
    }
}
