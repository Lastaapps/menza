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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.material3.DrawerValue.Open
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.androidPredictiveBackAnimatable
import com.arkivanov.decompose.extensions.compose.stack.animation.predictiveback.predictiveBackAnimation
import com.arkivanov.decompose.extensions.compose.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.arkivanov.essenty.backhandler.BackCallback
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.info.ui.component.InfoContent
import cz.lastaapps.menza.features.main.ui.component.DrawerContent
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.Info
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.LicenseNotices
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.Osturak
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.PrivacyPolicy
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.Settings
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.Today
import cz.lastaapps.menza.features.main.ui.navigation.MainComponent.Child.Week
import cz.lastaapps.menza.features.main.ui.screen.MainScreen
import cz.lastaapps.menza.features.main.ui.vm.MainViewModel
import cz.lastaapps.menza.features.other.ui.node.LicenseContent
import cz.lastaapps.menza.features.other.ui.node.OsturakContent
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsHubContent
import cz.lastaapps.menza.features.starting.ui.component.PolicyContent
import cz.lastaapps.menza.features.today.ui.navigation.TodayContent
import cz.lastaapps.menza.features.week.ui.node.WeekContent
import cz.lastaapps.menza.ui.locals.LocalMayBeFlipCover
import kotlinx.coroutines.launch

@OptIn(ExperimentalDecomposeApi::class)
@Composable
internal fun MainContent(
    component: MainComponent,
    modifier: Modifier = Modifier,
    hostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val mainViewModel: MainViewModel = component.viewModel

    val state by mainViewModel.flowState
    HandleAppear(mainViewModel)
    if (!state.isReady) return

    val drawerInitial = if (state.selectedMenza == null) Open else Closed
    val drawerState = rememberDrawerState(drawerInitial)
    val scope = rememberCoroutineScope()

    HandleLowBalance(
        lowBalance = state.showLowBalance,
        hostState = hostState,
        onDismiss = mainViewModel::dismissLowBalance,
    )

    val stack by component.content.subscribeAsState()

    MainScreen(
        currentDest = MainNavTarget.fromChild(stack.active.instance),
        drawerState = drawerState,
        settingsEverOpened = state.settingsViewed,
        hostState = hostState,
        selectedMenza = state.selectedMenza,
        alternativeNavigation = state.alternativeNavigation,
        isFlip = state.isFlip && LocalMayBeFlipCover.current,
        onNavItemTopBar = component::push,
        onNavItemRoot = component::pushRoot,
        drawerContent = {
            DrawerContent(
                component = component.drawerComponent,
                drawerState = drawerState,
                snackbarHostState = hostState,
            )
        },
        content = {
            // this section is responsible for properly handling back navigation
            // while using the alternative navigation
            val isAlternativeTrapEnabled = drawerState.isClosed && state.alternativeNavigation
            val alternativeCallback = remember {
                BackCallback(isAlternativeTrapEnabled) {
                    scope.launch { drawerState.open() }
                }
            }
            SideEffect { alternativeCallback.isEnabled = isAlternativeTrapEnabled }
            DisposableEffect(component.backHandler) {
                component.backHandler.register(alternativeCallback)
                onDispose { component.backHandler.unregister(alternativeCallback) }
            }

            Children(
                stack = stack,
                animation = predictiveBackAnimation(
                    backHandler = component.backHandler,
                    fallbackAnimation = stackAnimation(fade() + scale()),
                    selector = { backEvent, _, _ -> androidPredictiveBackAnimatable(backEvent) },
                    onBack = component::pop,
                ),
            ) {
                val onOsturak = { component.push(MainNavTarget.Osturak) }
                Surface {
                    when (val instance = it.instance) {
                        is Info -> InfoContent(instance.component, onOsturak, hostState)
                        is LicenseNotices -> LicenseContent(instance.component)
                        is Osturak -> OsturakContent(instance.component)
                        is PrivacyPolicy -> PolicyContent(instance.component) { component.pop() }
                        is Settings -> SettingsHubContent(instance.component)
                        is Today -> TodayContent(instance.component, onOsturak, hostState)
                        is Week -> WeekContent(instance.component, onOsturak, hostState)
                    }
                }
            }
        },
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun HandleLowBalance(
    lowBalance: Boolean,
    hostState: SnackbarHostState,
    onDismiss: () -> Unit,
) {
    val message = stringResource(id = R.string.wallet_low_balance)
    LaunchedEffect(lowBalance) {
        if (lowBalance) {
            hostState.showSnackbar(message)
            onDismiss()
        }
    }
}
