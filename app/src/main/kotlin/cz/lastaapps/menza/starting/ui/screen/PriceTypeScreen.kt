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

package cz.lastaapps.menza.starting.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.core.ui.vm.HandleAppear
import cz.lastaapps.core.ui.vm.HandleDismiss
import cz.lastaapps.menza.R
import cz.lastaapps.menza.settings.domain.model.PriceType.Discounted
import cz.lastaapps.menza.settings.domain.model.PriceType.Normal
import cz.lastaapps.menza.starting.ui.vm.PriceTypeState
import cz.lastaapps.menza.starting.ui.vm.PriceTypeViewModel
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.MenzaPadding
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PriceTypeScreen(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PriceTypeViewModel = koinViewModel(),
) {
    PriceTypeEffects(
        viewModel,
        onSelected = onDone,
    )

    PriceTypeContent(
        onNormal = { viewModel.selectType(Normal) },
        onDiscounted = { viewModel.selectType(Discounted) },
        modifier = modifier,
    )
}

@Composable
private fun PriceTypeEffects(
    viewModel: PriceTypeViewModel,
    onSelected: () -> Unit,
) {
    HandleAppear(viewModel)

    HandleDismiss(
        viewModel,
        PriceTypeState::isSelected,
        PriceTypeViewModel::dismissSelected,
        onSelected,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PriceTypeContent(
    onNormal: () -> Unit,
    onDiscounted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Column(
                    modifier = Modifier
                        .padding(MenzaPadding.Medium),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        MenzaPadding.Medium,
                        Alignment.CenterVertically
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
                        verticalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
                    ) {
                        Button(
                            onClick = onNormal,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.panel_price_normal))
                        }

                        Button(
                            onClick = onDiscounted,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(stringResource(R.string.panel_price_discounted))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PriceTypePreview() {
    AppTheme() {
        PriceTypeContent(onNormal = {}, onDiscounted = {})
    }
}
