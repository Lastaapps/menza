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

package cz.lastaapps.menza.ui.dests.settings.modules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import cz.lastaapps.menza.ui.dests.settings.store.imageSize
import kotlin.math.roundToInt

private const val imageSizeMin = .5f
private const val imageSizeMax = 3f

@Composable
fun ImageSizeSetting(settingsViewModel: SettingsViewModel, modifier: Modifier = Modifier) {
    val savedProgress by settingsViewModel.sett.imageSize.collectAsState()
    var progress by remember(savedProgress) { mutableStateOf(savedProgress) }

    Column(modifier) {
        Text(stringResource(R.string.settings_image_size_title))
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                value = progress,
                onValueChange = { progress = it },
                onValueChangeFinished = { settingsViewModel.setImageSize(progress) },
                valueRange = imageSizeMin..imageSizeMax,
                steps = (10 * (imageSizeMax - imageSizeMin)).toInt() + -1,
                modifier = Modifier.weight(1f),
            )

            val width = with(LocalDensity.current) { 48.sp.toDp() }
            Text(text = "${(progress * 100).roundToInt()}%", Modifier.width(width))
        }
    }
}