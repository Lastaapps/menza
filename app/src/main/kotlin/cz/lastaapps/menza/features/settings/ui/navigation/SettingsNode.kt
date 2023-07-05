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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.menza.features.settings.ui.screens.SettingsScreen
import cz.lastaapps.menza.features.settings.ui.vm.SettingsViewModel
import cz.lastaapps.menza.ui.theme.MenzaPadding
import org.koin.androidx.compose.koinViewModel

class SettingsNode(
    buildContext: BuildContext,
) : Node(buildContext) {
    @Composable
    override fun View(modifier: Modifier) {

        val viewModel: SettingsViewModel = koinViewModel()
        val state by viewModel.flowState

        HandleAppear(appearing = viewModel)

        // dismiss initial red dot
        LaunchedEffect(Unit) { viewModel.markAsViewed() }

        SettingsScreen(
            modifier = modifier
                .fillMaxSize()
                .padding(MenzaPadding.More.Screen),
            appTheme = state.appTheme,
            darkMode = state.darkMode,
            onChooseTheme = { /*TODO*/ },
            priceType = state.priceType,
            onDiscounterPrices = viewModel::setPriceType,
            downloadOnMetered = state.downloadOnMetered,
            onDownloadOnMetered = viewModel::setDownloadOnMetered,
            initialMenzaBehaviour = state.initialMenzaBehaviour,
            onInitialMenzaBehaviour = viewModel::setInitMenzaBehaviour,
            menzaList = state.menzaList,
            selectedMenza = state.selectedMenza,
            onSelectedMenza = viewModel::setSelectedMenza,
            showAbout = true,
            onAboutClicked = { /*TODO*/ },
            onPrivacyPolicy = { /*TODO*/ },
            onFullRefresh = { /*TODO*/ },
            onCrashesDialog = { /*TODO*/ },
        )
    }
}
