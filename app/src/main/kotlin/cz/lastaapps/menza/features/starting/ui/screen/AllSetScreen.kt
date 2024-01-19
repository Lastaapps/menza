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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.starting.ui.vm.AllSetViewModel
import cz.lastaapps.menza.ui.components.AppIcon
import cz.lastaapps.menza.ui.theme.Padding

@Composable
internal fun AllSetScreen(
    onDone: () -> Unit,
    viewModel: AllSetViewModel,
    modifier: Modifier = Modifier,
) {
    AllSetContent(
        onDone = {
            viewModel.onFinished()
            onDone()
        },
        modifier = modifier,
    )
}

@Composable
private fun AllSetContent(
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            Padding.Medium,
            Alignment.CenterVertically,
        ),
    ) {
        AppIcon(size = AllSetScreen.iconSize)

        Text(
            text = stringResource(R.string.all_set_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(R.string.all_set_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )

        Button(onClick = onDone) {
            Text(stringResource(R.string.all_set_button))
        }
    }
}

private object AllSetScreen {
    val iconSize = 96.dp
}
