/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bumble.appyx.navigation.integration.NodeComponentActivity
import com.bumble.appyx.navigation.integration.NodeHost
import com.bumble.appyx.navigation.platform.AndroidLifecycle
import cz.lastaapps.menza.features.root.ui.RootNode
import cz.lastaapps.menza.ui.locals.LocalActivityViewModelOwner
import cz.lastaapps.menza.ui.locals.WithFoldingFeature
import cz.lastaapps.menza.ui.locals.WithLocalWindowSizes


class MainActivity : NodeComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var isReady = false
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition {
            !isReady
        }

        setContent {
            ApplyProviders {
                NodeHost(
                    lifecycle = AndroidLifecycle(lifecycle),
                    integrationPoint = appyxV2IntegrationPoint,
                    modifier = Modifier.fillMaxSize(),
                ) { buildContext ->
                    RootNode(buildContext) { isReady = true }
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
