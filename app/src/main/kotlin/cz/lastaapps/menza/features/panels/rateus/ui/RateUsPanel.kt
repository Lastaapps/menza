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

package cz.lastaapps.menza.features.panels.rateus.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.menza.R.string
import cz.lastaapps.menza.ui.theme.MenzaPadding
import cz.lastaapps.menza.ui.util.PreviewWrapper

@Composable
internal fun RateUsPanel(
    viewModel: RateUsViewModel,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.flowState.value

    RateUsPanel(
        state = state,
        onPlayClick = viewModel::ratePlayStore,
        onGithubClick = viewModel::rateGithub,
        onLater = viewModel::later,
        onDismiss = viewModel::dismiss,
        modifier = modifier,
    )
}

@Composable
private fun RateUsPanel(
    state: RateUsViewModel.State,
    onPlayClick: () -> Unit,
    onGithubClick: () -> Unit,
    onLater: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = string.rate_us_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(id = string.rate_us_description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(MenzaPadding.MidSmall))

        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MenzaPadding.Smaller),
        ) {

            if (!state.githubRated) {
                GithubButton(onClick = onGithubClick, modifier = Modifier.fillMaxWidth())
            }

            if (!state.playRated) {
                PlayButton(onClick = onPlayClick, modifier = Modifier.fillMaxWidth())
            }

            DismissOrLater(
                onLater = onLater,
                onDismiss = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun PlayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xff01875f),
        contentColor = Color(0xffe6f3ef),
    )
    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.width(MenzaPadding.Medium))

        Image(
            painter = painterResource(id = cz.lastaapps.common.R.drawable.ic_play_store),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(MenzaPadding.Small))

        Text(text = stringResource(id = string.rate_us_button_play))

        Spacer(modifier = Modifier.width(MenzaPadding.Medium))
    }
}

@Composable
private fun GithubButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xff24292e),
        contentColor = Color(0xfffafbfc),
    )
    Button(
        onClick = onClick,
        colors = colors,
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.width(MenzaPadding.Medium))

        Image(
            painter = painterResource(id = cz.lastaapps.common.R.drawable.ic_github),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(MenzaPadding.Small))

        Text(text = stringResource(id = string.rate_us_button_github))

        Spacer(modifier = Modifier.width(MenzaPadding.Medium))
    }
}

@Composable
private fun DismissOrLater(
    onLater: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MenzaPadding.Small),
    ) {
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f),
        ) {
            Text(text = stringResource(id = string.rate_us_button_dismiss))
        }

        OutlinedButton(
            onClick = onLater,
            modifier = Modifier.weight(1f),
        ) {
            Text(text = stringResource(id = string.rate_us_button_later))
        }
    }
}

@Preview
@Composable
private fun RatePanelPreview() = PreviewWrapper {
    RateUsPanel(
        RateUsViewModel.State(),
        {},
        {},
        {},
        {},
    )
}
