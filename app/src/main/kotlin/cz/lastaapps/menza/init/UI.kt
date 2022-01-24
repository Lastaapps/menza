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

package cz.lastaapps.menza.init

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.consumeEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InitDecision(
    viewModel: InitViewModel,
    onDrawReady: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isReady by viewModel.isDone.collectAsState()

    if (isReady) {
        content()
        return
    }
    val downloadStarted by viewModel.startedDownloading.collectAsState()
    if (!downloadStarted) {
        return
    }

    val snackbarHost = remember { SnackbarHostState() }
    LaunchedEffect(viewModel.errors) {
        viewModel.errors.consumeEach {
            snackbarHost.showSnackbar(it.toString())
        }
    }

    Scaffold(
        modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHost) },
        content = {
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                InitContent(viewModel = viewModel, Modifier.fillMaxSize())
            }
        })

    // Splashscreen is shown until actual download started, so there are no blink while
    // hasData() queries are executed
    onDrawReady()
}

@Composable
internal fun InitContent(viewModel: InitViewModel, modifier: Modifier = Modifier) {

    val progress by viewModel.progressIndicator.collectAsState()
    val message by viewModel.progressMessage.collectAsState()
    val failed by viewModel.failed.collectAsState()

    Box(modifier, contentAlignment = Alignment.Center) {
        Surface(
            Modifier.animateContentSize(),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "Downloading default data...",
                    style = MaterialTheme.typography.headlineMedium
                )
                if (!failed) {
                    val animatedProgress by animateFloatAsState(targetValue = progress)
                    CircularProgressIndicator(animatedProgress)
                    Text(text = "Downloading $message")
                } else {
                    IconButton(onClick = { viewModel.requestRefresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                    }
                    Text("An error occurred! Retry?")
                }
            }
        }
    }
}


