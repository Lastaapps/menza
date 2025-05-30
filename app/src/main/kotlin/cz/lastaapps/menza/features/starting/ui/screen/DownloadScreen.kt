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

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.starting.ui.util.text
import cz.lastaapps.menza.features.starting.ui.vm.DownloadDataState
import cz.lastaapps.menza.features.starting.ui.vm.DownloadViewModel
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.HandleError

@Composable
internal fun DownloadScreen(
    onComplete: () -> Unit,
    viewModel: DownloadViewModel,
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    DownloadEffects(viewModel, hostState, onComplete)

    DownloadContent(
        state = viewModel.flowState.value,
        onRefresh = viewModel::retry,
        modifier = modifier,
    )
}

@Composable
private fun DownloadEffects(
    viewModel: DownloadViewModel,
    hostState: SnackbarHostState,
    onComplete: () -> Unit,
) {
    HandleError(viewModel, hostState)

    val isDone = viewModel.flowState.value.isDone
    val onCompleteLambda by rememberUpdatedState(onComplete)
    LaunchedEffect(isDone) {
        if (isDone) {
            onCompleteLambda()
            viewModel.dismissDone()
        }
    }
}

@Composable
private fun DownloadContent(
    state: DownloadDataState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement =
                Arrangement.spacedBy(
                    Padding.Medium,
                    Alignment.CenterVertically,
                ),
            modifier =
                Modifier
                    .sizeIn(maxWidth = DownloadUI.maxWidth)
                    .fillMaxWidth()
                    .padding(Padding.Small)
                    .animateContentSize(),
        ) {
            Text(
                stringResource(R.string.init_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
            )

            Crossfade(
                targetState = state.isLoading to state.isReady,
                modifier = Modifier.fillMaxWidth(),
                label = "download_state",
            ) { (isLoading, isReady) ->
                when {
                    isLoading && isReady -> {
                        Column(verticalArrangement = Arrangement.spacedBy(Padding.Small)) {
                            val animatedProgress =
                                animateFloatAsState(
                                    targetValue = state.downloadProgress.progress,
                                    animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                                    label = "download_progress",
                                )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                LinearProgressIndicator(
                                    progress = { animatedProgress.value },
                                    modifier = Modifier.weight(1f),
                                )

                                val percentValue = state.downloadProgress.progress * 100
                                Text(
                                    "%3.0f %%".format(percentValue),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                            Text(state.downloadProgress.text)
                        }
                    }

                    !isLoading && isReady -> {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Padding.Small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            IconButton(onClick = onRefresh) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                            }
                            Text(stringResource(R.string.init_error))
                        }
                    }

                    else -> {
                        Box(Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}

private object DownloadUI {
    val maxWidth = 256.dp
}
