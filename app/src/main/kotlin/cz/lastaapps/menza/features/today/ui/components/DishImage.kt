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

package cz.lastaapps.menza.features.today.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest.Builder
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.placeholder
import cz.lastaapps.menza.R.string


@Composable
internal fun DishImageRatio(
    photoLink: String,
    loadImmediately: Boolean,
    modifier: Modifier = Modifier,
) {
    val imageModifier = modifier
        .fillMaxWidth()
//        .aspectRatio(16f / 9f)
        .aspectRatio(4f / 3f)

    DishImage(
        photoLink = photoLink,
        loadImmediately = loadImmediately,
        modifier = imageModifier
    )
}

@Composable
internal fun DishImage(
    photoLink: String,
    loadImmediately: Boolean,
    modifier: Modifier = Modifier,
) {
    var retryHash by remember { mutableStateOf(0) }
    var userAllowed by rememberSaveable(photoLink) { mutableStateOf(false) }
    val canDownload = loadImmediately || userAllowed

    val imageRequest = with(Builder(LocalContext.current)) {
        diskCacheKey(photoLink)
        memoryCacheKey(photoLink)
        crossfade(true)
        setParameter("retry_hash", retryHash)
        // if user is not on a metered network, images are going to be loaded from cache
        if (canDownload)
            data(photoLink)
        else
            data("https://userisonmeterednetwork.localhost/")
        //data(null) - cache is not working
        build()
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
    ) {
        SubcomposeAsyncImage(
            imageRequest,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    Modifier
                        .placeholder(
                            true, color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.medium,
                            highlight = PlaceholderHighlight.fade(
                                highlightColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                        .clickable { retryHash++ }
                )
            },
            error = {
                Box(
                    Modifier.clickable { retryHash++; userAllowed = true },
                    contentAlignment = Alignment.Center,
                ) {
                    if (canDownload)
                        Icon(
                            Icons.Default.Refresh,
                            stringResource(string.today_list_image_load_failed)
                        )
                    else
                        Icon(
                            Icons.Default.Download,
                            stringResource(string.today_list_image_metered)
                        )
                }
            },
        )
    }
}
