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

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BalanceThresholdSlider(
    title: String,
    threshold: Int,
    onThreshold: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(SettingsTokens.itemPadding)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Padding.Small),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SettingsTitle(text = title, modifier = Modifier.weight(1f))
            Text(threshold.thresholdText())
        }

        var localThreshold by remember(threshold) { mutableFloatStateOf(threshold.toFloat()) }

        val interactionSource = remember { MutableInteractionSource() }

        Slider(
            value = localThreshold,
            onValueChange = { localThreshold = it },
            onValueChangeFinished = { onThreshold(localThreshold.roundToInt()) },
            modifier = Modifier.padding(horizontal = Padding.Tiny),
            valueRange = 0f..420f,
            steps = 420 / 10 - 1,
            interactionSource = interactionSource,
            thumb = {
                Label(
                    label = {
                        PlainTooltip(
                            caretSize = TooltipDefaults.caretSize,
                            modifier = Modifier.wrapContentWidth(),
                            shape = CircleShape,
                        ) {
                            Text(
                                it.value.roundToInt().thresholdText(),
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

@Composable
private fun Int.thresholdText() =
    if (this > 0) {
        "%d Kč".format(this)
    } else {
        stringResource(R.string.settings_balance_threshold_disabled)
    }

@Preview
@Composable
private fun BalanceThresholdSliderPreview() = PreviewWrapper {
    BalanceThresholdSlider(
        title = stringResource(id = R.string.settings_balance_threshold_title),
        threshold = 0,
        {},
    )
    BalanceThresholdSlider(
        title = stringResource(id = R.string.settings_balance_threshold_title),
        threshold = 256,
        {},
    )
}
