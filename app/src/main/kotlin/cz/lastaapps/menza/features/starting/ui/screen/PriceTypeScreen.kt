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

package cz.lastaapps.menza.features.starting.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.core.ui.vm.HandleDismiss
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.features.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.features.starting.ui.vm.PriceTypeState
import cz.lastaapps.menza.features.starting.ui.vm.PriceTypeViewModel
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.Padding

@Composable
internal fun PriceTypeScreen(
    onComplete: () -> Unit,
    viewModel: PriceTypeViewModel,
    modifier: Modifier = Modifier,
) {
    PriceTypeEffects(
        viewModel,
        onSelect = onComplete,
    )

    PriceTypeContent(
        onNormal = { viewModel.selectType(Normal) },
        onDiscount = { viewModel.selectType(Discounted) },
        modifier = modifier,
    )
}

@Composable
private fun PriceTypeEffects(
    viewModel: PriceTypeViewModel,
    onSelect: () -> Unit,
) {
    HandleDismiss(
        viewModel,
        PriceTypeState::isSelected,
        PriceTypeViewModel::dismissSelected,
        onSelect,
    )
}

@Composable
private fun PriceTypeContent(
    onNormal: () -> Unit,
    onDiscount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Column(
                modifier =
                    Modifier
                        .padding(Padding.Medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.spacedBy(
                        Padding.Medium,
                        Alignment.CenterVertically,
                    ),
            ) {
                Text(
                    text = stringResource(R.string.panel_price_title),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = stringResource(R.string.panel_price_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )

                Column(
                    modifier = Modifier.width(IntrinsicSize.Max),
                    verticalArrangement = Arrangement.spacedBy(Padding.Small),
                ) {
                    Button(
                        onClick = onNormal,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.panel_price_normal))
                    }

                    Button(
                        onClick = onDiscount,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(R.string.panel_price_discounted))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PriceTypePreview() {
    AppTheme {
        PriceTypeContent(onNormal = {}, onDiscount = {})
    }
}
