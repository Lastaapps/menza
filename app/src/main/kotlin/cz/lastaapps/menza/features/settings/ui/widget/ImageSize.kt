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

package cz.lastaapps.menza.features.settings.ui.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlin.math.roundToInt

private const val imageSizeMin = .5f
private const val imageSizeMax = 3f

@Composable
internal fun ImageSizeSetting(
    progress: Float,
    onProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var uiProgress by remember(progress) { mutableStateOf(progress) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Padding.Medium),
        ) {
            SettingsTitle(
                stringResource(R.string.settings_image_size_title),
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "${(uiProgress * 100).roundToInt()}%",
                style = SettingsTokens.subtitleStyle,
            )
        }
        Slider(
            value = uiProgress,
            onValueChange = { uiProgress = it },
            onValueChangeFinished = { onProgressChanged(uiProgress) },
            valueRange = imageSizeMin..imageSizeMax,
            steps = (10 * (imageSizeMax - imageSizeMin)).toInt() + -1,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ImageSizeSettingPreview() = PreviewWrapper {
    ImageSizeSetting(progress = imageSizeMin, onProgressChanged = {})
    ImageSizeSetting(progress = imageSizeMin.plus(imageSizeMax) / 2, onProgressChanged = {})
    ImageSizeSetting(progress = imageSizeMax, onProgressChanged = {})
}
