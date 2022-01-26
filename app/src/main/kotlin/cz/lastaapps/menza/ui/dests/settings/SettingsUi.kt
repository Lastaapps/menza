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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.ui.dests.others.AboutUi
import cz.lastaapps.menza.ui.dests.others.ReportDialog
import cz.lastaapps.menza.ui.dests.others.sendReport
import cz.lastaapps.menza.ui.dests.settings.modules.DarkThemeSettings
import cz.lastaapps.menza.ui.dests.settings.modules.InitMenzaUI
import cz.lastaapps.menza.ui.dests.settings.store.*
import cz.lastaapps.menza.ui.layout.menza.MenzaViewModel

@Composable
fun SettingsUI(
    navController: NavController,
    viewModel: SettingsViewModel,
    menzaViewModel: MenzaViewModel,
    enableAbout: Boolean,
    modifier: Modifier = Modifier,
    aboutShown: Boolean = false,
    onAboutClicked: () -> Unit = {},
) {
    if (aboutShown) {
        AboutUi(
            navController = navController,
            scrollState = rememberScrollState(),
            Modifier.fillMaxSize()
        )
        return
    }

    val scrollState = rememberScrollState()

    BoxWithConstraints(modifier.fillMaxWidth()) {
        val width = minWidth

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                Modifier
                    .verticalScroll(scrollState)
                    .width(min(width, 300.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    "Settings",
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
        //TODO use Material 3 Switch when available
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
            title = "Use system color theme",
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
        title = "Show discounted prices",
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
        title = "Auto download images on metered networks",
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
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val uriHandler = LocalUriHandler.current

        Button(
            onClick = { navController.navigate(Dest.R.privacyPolicy) },
            Modifier.fillMaxWidth()
        ) {
            Text(text = "Privacy Policy")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (enableAbout)
                Button(
                    onClick = onAboutClicked, modifier = Modifier.weight(1f)
                ) { Text(text = "About") }

            Button(
                onClick = { uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.menza") },
                modifier = Modifier.weight(1f)
            ) { Text(text = "Rate us!") }
        }

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
        Text(text = "Full cache refresh")
    }
}

@Composable
private fun ReportButton(modifier: Modifier = Modifier) {
    var shown by rememberSaveable { mutableStateOf(false) }

    Button(onClick = { shown = true }, modifier) {
        Text("Report an error")
    }

    val context = LocalContext.current
    ReportDialog(shown, { shown = false }) {
        sendReport(context, it)
        shown = false
    }
}

