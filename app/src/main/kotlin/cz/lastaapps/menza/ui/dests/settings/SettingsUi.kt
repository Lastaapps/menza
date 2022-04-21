/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.ui.dests.settings

import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import cz.lastaapps.menza.R
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.ui.dests.others.AboutUi
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport
import cz.lastaapps.menza.ui.dests.settings.modules.DarkThemeSettings
import cz.lastaapps.menza.ui.dests.settings.modules.FullReloadDialog
import cz.lastaapps.menza.ui.dests.settings.modules.InitMenzaUI
import cz.lastaapps.menza.ui.dests.settings.store.*
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel

@Composable
fun SettingsUI(
    navController: NavController,
    viewModel: SettingsViewModel,
    menzaViewModel: MenzaViewModel,
    settingsViewModel: SettingsViewModel,
    enableAbout: Boolean,
    modifier: Modifier = Modifier,
    aboutShown: Boolean = false,
    onAboutClicked: () -> Unit = {},
) {
    LaunchedEffect(Unit) { settingsViewModel.setSettingsEverOpened(true) }
    if (aboutShown) {
        AboutUi(
            navController = navController,
            scrollState = rememberScrollState(),
            Modifier.fillMaxSize()
        )
        return
    }

    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        val width = minWidth

        Box(
            Modifier
                .verticalScroll(scrollState)
                .width(min(width, 300.dp)),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                DarkThemeSettings(viewModel, Modifier.fillMaxWidth())

                Switches(viewModel, Modifier.fillMaxWidth())

                InitMenzaUI(
                    menzaViewModel = menzaViewModel,
                    settingsViewModel = viewModel,
                    Modifier.fillMaxWidth(),
                )

                Buttons(
                    navController, enableAbout, onAboutClicked, viewModel,
                    Modifier.fillMaxWidth(.8f)
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = { onClick() })
    }
}

@Composable
private fun Switches(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        UseThemeSettings(viewModel = viewModel)
        PriceSettings(viewModel = viewModel)
        ImagesOnMeteredSetting(viewModel = viewModel)
    }
}

@Composable
private fun UseThemeSettings(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    if (viewModel.sett.isSystemThemeAvailable()) {
        val mode by viewModel.sett.systemTheme.collectAsState()

        SettingsSwitch(
            title = stringResource(R.string.settings_switch_system_theme),
            checked = mode,
            onClick = { viewModel.setUseSystemTheme(!mode) },
            modifier = modifier,
        )
    }
}

@Composable
private fun PriceSettings(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val mode by viewModel.sett.priceType.collectAsState()

    SettingsSwitch(
        title = stringResource(R.string.settings_switch_price),
        checked = mode is PriceType.Discounted,
        onClick = {
            viewModel.setPriceType(
                if (mode is PriceType.Discounted) PriceType.Normal else PriceType.Discounted
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun ImagesOnMeteredSetting(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val mode by viewModel.sett.imagesOnMetered.collectAsState()

    SettingsSwitch(
        title = stringResource(R.string.settings_switch_metered),
        checked = mode,
        onClick = { viewModel.setImagesOnMetered(!mode) },
        modifier = modifier,
    )
}

@Composable
private fun Buttons(
    navController: NavController,
    enableAbout: Boolean, onAboutClicked: () -> Unit,
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current

        val shareText = stringResource(R.string.settings_button_share_text)
        Button(
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, null))
            },
            modifier = Modifier.fillMaxWidth(),
        ) { IconAndText(Icons.Default.Share, R.string.settings_button_share) }

        Button(
            onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.menza") },
            modifier = Modifier.fillMaxWidth(),
        ) { IconAndText(Icons.Default.Star, R.string.settings_button_rate) }

        if (enableAbout) {
            Button(
                onClick = onAboutClicked, modifier = Modifier.fillMaxWidth()
            ) { IconAndText(Icons.Default.Info, R.string.settings_button_about) }
        }

        Button(
            onClick = { navController.navigate(Dest.R.privacyPolicy) },
            Modifier.fillMaxWidth()
        ) { IconAndText(Icons.Default.Security, R.string.settings_button_privacy_policy) }

        ReportButton(Modifier.fillMaxWidth())

        FullDataReload(viewModel = viewModel, Modifier.fillMaxWidth())
    }
}

@Composable
private fun FullDataReload(viewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    var showFullReload by rememberSaveable { mutableStateOf(false) }
    FullReloadDialog(
        shown = showFullReload,
        onDismissRequest = { showFullReload = false }) {
        viewModel.fullRefresh()
    }
    Button(onClick = { showFullReload = true }, modifier) {
        IconAndText(Icons.Default.Refresh, R.string.settings_button_reload)
    }
}

@Composable
private fun ReportButton(modifier: Modifier = Modifier) {
    var shown by rememberSaveable { mutableStateOf(false) }

    Button(onClick = { shown = true }, modifier) {
        IconAndText(Icons.Default.BugReport, R.string.settings_button_report)
    }

    val context = LocalContext.current
    ReportDialog(shown, { shown = false }) {
        sendReport(context, it)
        shown = false
    }
}

@Composable
private fun IconAndText(icon: ImageVector, @StringRes textId: Int) =
    IconAndText(icon, stringResource(textId))

@Composable
private fun IconAndText(icon: ImageVector, text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null)
        Text(text, textAlign = TextAlign.Center)
    }
}

