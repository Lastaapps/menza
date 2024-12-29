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

package cz.lastaapps.menza.features.today.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy.DISABLED
import coil3.request.ImageRequest.Builder
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.components.BaseDialog
import cz.lastaapps.menza.ui.theme.Padding

@Composable
internal fun ImagePreviewDialog(
    videoFeedUrl: String,
    onDismissRequest: () -> Unit,
) {
    BaseDialog(
        onDismissRequest = onDismissRequest,
    ) {
        val imageRequest =
            with(Builder(LocalContext.current)) {
                diskCachePolicy(DISABLED)
                memoryCachePolicy(DISABLED)
                data(videoFeedUrl)
                build()
            }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                Icon(Icons.Default.Videocam, null)
                Text(
                    text = stringResource(id = R.string.today_list_video_title),
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                )
            }

            SubcomposeAsyncImage(
                imageRequest,
                contentDescription = null,
                contentScale = ContentScale.Inside,
                loading = {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    Padding.Small,
                                    Alignment.CenterHorizontally,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.ErrorOutline, null)
                            Text(stringResource(id = R.string.today_list_video_error))
                        }
                    }
                },
                modifier = Modifier.aspectRatio(4f / 3f),
            )
        }
    }
}
