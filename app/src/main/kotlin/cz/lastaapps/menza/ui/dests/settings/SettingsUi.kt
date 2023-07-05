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

package cz.lastaapps.menza.ui.dests.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavController
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.ui.components.InitMenzaUI
import cz.lastaapps.menza.ui.dests.others.AboutUi
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

//                DarkThemeChooser(viewModel, Modifier.fillMaxWidth())

                // Switches(viewModel, Modifier.fillMaxWidth())

                // ImageSizeSetting(settingsViewModel)

                InitMenzaUI(
                    menzaViewModel = menzaViewModel,
                    settingsViewModel = viewModel,
                    Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
