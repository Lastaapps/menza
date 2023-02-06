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

package cz.lastaapps.menza.ui.dests.panels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.settings.data.priceType
import cz.lastaapps.menza.settings.domain.model.PriceType
import cz.lastaapps.menza.ui.dests.settings.SettingsViewModel
import kotlinx.coroutines.flow.map

@Composable
fun priceTypeUnspecifiedState(settingsViewModel: SettingsViewModel): State<Boolean> {
    return remember {
        settingsViewModel.sett.priceType.map { it == PriceType.Unset }
    }.collectAsState(false)
}

@Composable
fun PriceTypeUnspecified(
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val onPriceType: (PriceType) -> Unit = { settingsViewModel.setPriceType(it) }

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            stringResource(R.string.panel_price_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Max)
        ) {
            Button(
                onClick = { onPriceType(PriceType.Discounted) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    stringResource(R.string.panel_price_discounted),
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = { onPriceType(PriceType.Normal) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    stringResource(R.string.panel_price_normal),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
