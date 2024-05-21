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

package cz.lastaapps.menza.features.today.ui.widget

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper

private const val imageSizeMin = .5f
private const val imageSizeMax = 2f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ImageSizeSetting(
    progress: Float,
    onProgressChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var uiProgress by remember(progress) { mutableFloatStateOf(progress) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
    ) {
        Text(
            stringResource(R.string.settings_image_size_title),
            modifier = Modifier.basicMarquee(),
            style = MaterialTheme.typography.titleLarge,
            softWrap = false,
            maxLines = 1,
        )

        val interactionSource = remember { MutableInteractionSource() }
        val contentDescription = stringResource(R.string.settings_image_size_title)
        Slider(
            value = uiProgress,
            onValueChange = { uiProgress = it },
            onValueChangeFinished = { onProgressChanged(uiProgress) },
            valueRange = imageSizeMin..imageSizeMax,
            steps = (10 * (imageSizeMax - imageSizeMin)).toInt() + -1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Padding.Medium)
                .semantics { this.contentDescription = contentDescription },
            interactionSource = interactionSource,
            thumb = {
                Label(
                    label = {
                        PlainTooltip(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(vertical = Padding.Smaller),
                        ) {
                            Text(
                                "%.0f%%".format(it.value * 100),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    },
                    interactionSource = interactionSource,
                ) {
                    SliderDefaults.Thumb(interactionSource = interactionSource)
                }
            },
        )
    }
}

@Preview
@Composable
private fun ImageSizeSettingPreview() = PreviewWrapper {
    Column {
        ImageSizeSetting(progress = imageSizeMin, onProgressChanged = {})
        ImageSizeSetting(progress = imageSizeMin.plus(imageSizeMax) / 2, onProgressChanged = {})
        ImageSizeSetting(progress = imageSizeMax, onProgressChanged = {})
    }
}
