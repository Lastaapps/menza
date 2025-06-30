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

package cz.lastaapps.menza.features.settings.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import cz.lastaapps.menza.features.other.ui.dialog.PrivacyDialog
import cz.lastaapps.menza.features.other.ui.dialog.WhatsNewDialog
import cz.lastaapps.menza.features.other.ui.widgets.CrashesDialog
import cz.lastaapps.menza.features.panels.crashreport.ui.CrashesViewModel
import cz.lastaapps.menza.features.panels.whatsnew.ui.vm.WhatsNewViewModel
import cz.lastaapps.menza.features.settings.ui.screens.AboutScreen
import cz.lastaapps.menza.features.settings.ui.screens.SettingsScreen
import cz.lastaapps.menza.features.settings.ui.vm.SettingsViewModel
import cz.lastaapps.menza.ui.components.layout.SplitLayout
import cz.lastaapps.menza.ui.locals.LocalWindowWidth
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.getOrCreateKoin
import org.koin.core.component.KoinComponent

internal interface SettingsComponent {
    val viewModel: SettingsViewModel
    val crashViewModel: CrashesViewModel
    val whatsNewViewModel: WhatsNewViewModel
}

internal class DefaultSettingsComponent(
    componentContext: ComponentContext,
) : SettingsComponent,
    KoinComponent,
    ComponentContext by componentContext {
    override val viewModel: SettingsViewModel = getOrCreateKoin()
    override val crashViewModel: CrashesViewModel = getOrCreateKoin()
    override val whatsNewViewModel: WhatsNewViewModel = getOrCreateKoin()
}

@Composable
internal fun SettingsContent(
    component: SettingsComponent,
    onChooseTheme: () -> Unit,
    onChooseDishLanguage: () -> Unit,
    onOsturak: () -> Unit,
    onLicense: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = component.viewModel
    val crashesViewModel: CrashesViewModel = component.crashViewModel
    val whatsNewViewModel: WhatsNewViewModel = component.whatsNewViewModel
    val state by viewModel.flowState

    // dismiss initial red dot
    LaunchedEffect(Unit) { viewModel.markAsViewed() }

    val normalScreen = LocalWindowWidth.current == WindowWidthSizeClass.Compact
    var showAbout by rememberSaveable { mutableStateOf(false) }
    var showPrivacyDialog by rememberSaveable { mutableStateOf(false) }
    var showCrashDialog by rememberSaveable { mutableStateOf(false) }
    var showWhatsNew by rememberSaveable { mutableStateOf(false) }

    val appSettings = state.appSettings ?: return
    val settingsScreen: @Composable () -> Unit = {
        SettingsScreen(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(Padding.More.Screen),
            appSettings = appSettings,
            appTheme = state.appTheme,
            preferredMenza = state.preferredMenza,
            onChooseTheme = onChooseTheme,
            onChooseDishLanguage = onChooseDishLanguage,
            onDiscounterPrices = viewModel::setPriceType,
            onCurrency = viewModel::setCurrency,
            onImagesOnMetered = viewModel::setDownloadOnMetered,
            onAlternativeNavigation = viewModel::setAlternativeNavigation,
            onBalanceThreshold = viewModel::setBalanceWarningThreshold,
            onInitialMenzaBehaviour = viewModel::setInitMenzaBehaviour,
            menzaList = state.menzaList,
            onSelectedMenza = viewModel::setSelectedMenza,
            showAbout = normalScreen,
            onAboutClick = { showAbout = true },
            onPrivacyPolicy = { showPrivacyDialog = true },
            onFullRefresh = viewModel::fullAppReload,
            onCrashesDialog = { showCrashDialog = true },
        )
    }
    val aboutScreen: @Composable () -> Unit = {
        AboutScreen(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(Padding.More.Screen),
            onOsturak = onOsturak,
            onLicense = onLicense,
            onShowWhatsNew = { showWhatsNew = true },
        )
    }

    if (normalScreen) {
        Crossfade(
            targetState = showAbout,
            label = "Settings/about",
        ) { showAboutTarget ->
            if (!showAboutTarget) {
                settingsScreen()
            } else {
                BackHandler { showAbout = false }
                aboutScreen()
            }
        }
    } else {
        SplitLayout(
            panel1 = settingsScreen,
            panel2 = aboutScreen,
        )
    }

    if (showPrivacyDialog) {
        PrivacyDialog(
            onDismissRequest = { showPrivacyDialog = false },
            showAccept = false,
            onAccept = {},
        )
    }

    if (showCrashDialog) {
        CrashesDialog(
            viewModel = crashesViewModel,
            onDismissRequest = { showCrashDialog = false },
        )
    }

    if (showWhatsNew) {
        WhatsNewDialog(
            viewModel = whatsNewViewModel,
            onDismissRequest = { showWhatsNew = false },
        )
    }
}
