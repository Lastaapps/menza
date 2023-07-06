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

package cz.lastaapps.menza.features.settings.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import cz.lastaapps.menza.features.other.ui.node.LicenseNode
import cz.lastaapps.menza.features.other.ui.node.OsturakNode
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsNavTarget.APP_THEME
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsNavTarget.LICENSE
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsNavTarget.OSTURAK
import cz.lastaapps.menza.features.settings.ui.navigation.SettingsNavTarget.SETTINGS
import cz.lastaapps.menza.features.settings.ui.nodes.AppThemeNode
import cz.lastaapps.menza.features.settings.ui.nodes.SettingsNode

class SettingsHubNode internal constructor(
    buildContext: BuildContext,
    private val backstack: BackStack<SettingsNavTarget> = BackStack(
        initialElement = SettingsNavTarget.SETTINGS,
        savedStateMap = buildContext.savedStateMap,
    ),
) : ParentNode<SettingsNavTarget>(backstack, buildContext) {
    override fun resolve(navTarget: SettingsNavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            SETTINGS -> SettingsNode(
                onChooseTheme = { backstack.push(APP_THEME) },
                onOsturak = { backstack.push(OSTURAK) },
                onLicense = { backstack.push(LICENSE) },
                buildContext = buildContext,
            )

            APP_THEME -> AppThemeNode(
                buildContext = buildContext,
                onDone = { backstack.pop() },
            )

            OSTURAK -> OsturakNode(
                buildContext = buildContext,
            )

            LICENSE -> LicenseNode(
                buildContext = buildContext,
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        Children(
            navModel = backstack,
            modifier = modifier,
            transitionHandler = rememberBackstackFader(),
        )
    }
}
