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

package cz.lastaapps.menza

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.navigation.integration.NodeComponentActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.root.ui.RootNode
import cz.lastaapps.menza.features.root.ui.RootViewModel
import cz.lastaapps.menza.ui.ApplyAppTheme
import cz.lastaapps.menza.ui.locals.LocalActivityViewModelOwner
import cz.lastaapps.menza.ui.locals.WithFoldingFeature
import cz.lastaapps.menza.ui.locals.WithLocalWindowSizes
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : NodeComponentActivity() {

    private val viewModel: RootViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isReady = false
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !isReady
        }

        enableEdgeToEdge()

        setContent {
            HandleAppear(viewModel)

            ApplyProviders {
                ApplyAppTheme(viewModel, this) {
                    NodeHost(
                        lifecycle = AndroidLifecycle(LocalLifecycleOwner.current.lifecycle),
                        integrationPoint = appyxV2IntegrationPoint,
                        modifier = Modifier.fillMaxSize(),
                    ) { buildContext ->
                        RootNode(buildContext, viewModel = viewModel) { isReady = true }
                    }
                }
            }
        }
    }

    @Composable
    private fun ApplyProviders(
        content: @Composable () -> Unit,
    ) {
        WithLocalWindowSizes(this) {
            WithFoldingFeature(this) {
                CompositionLocalProvider(
                    LocalActivityViewModelOwner provides this,
                ) {
                    content()
                }
            }
        }
    }
}
