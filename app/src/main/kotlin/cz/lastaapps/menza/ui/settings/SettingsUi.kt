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

package cz.lastaapps.menza.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import cz.lastaapps.menza.navigation.Dest
import cz.lastaapps.menza.ui.others.AboutUi
import cz.lastaapps.menza.ui.settings.store.PriceType
import cz.lastaapps.menza.ui.settings.store.isSystemThemeAvailable
import cz.lastaapps.menza.ui.settings.store.priceType
import cz.lastaapps.menza.ui.settings.store.systemTheme

@Composable
fun SettingsUI(
    navController: NavController,
    viewModel: SettingsViewModel,
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
    val uriHandler = LocalUriHandler.current

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )

                DarkThemeSettings(viewModel = viewModel)

                UseThemeSettings(viewModel = viewModel)

                PriceSettings(viewModel = viewModel)

                Button(onClick = { navController.navigate(Dest.R.privacyPolicy) }) {
                    Text(text = "Privacy Policy")
                }

                if (enableAbout)
                    Button(onClick = onAboutClicked) {
                        Text(text = "About")
                    }

                Button(onClick = {
                    uriHandler.openUri("https://play.google.com/store/apps/details?id=cz.lastaapps.menza")
                }) {
                    Text(text = "Rate us!")
                }
            }
        }
    }
}

@Composable
fun SettingsSwitch(
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
fun UseThemeSettings(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
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
fun PriceSettings(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
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


