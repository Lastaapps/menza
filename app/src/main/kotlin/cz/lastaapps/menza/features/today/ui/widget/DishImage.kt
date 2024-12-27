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

package cz.lastaapps.menza.features.today.ui.widget

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.Extras
import coil3.compose.SubcomposeAsyncImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest.Builder
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.menza.R.string
import cz.lastaapps.menza.ui.components.placeholders.PlaceholderHighlight
import cz.lastaapps.menza.ui.components.placeholders.fade
import cz.lastaapps.menza.ui.components.placeholders.placeholder
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlin.math.absoluteValue
import kotlin.random.Random

internal fun loadImmediately(
    downloadOnMetered: Boolean,
    isOnMetered: Boolean,
) = downloadOnMetered || !isOnMetered

@Composable
internal fun DishImageOrSupplement(
    dish: Dish,
    loadImmediately: Boolean,
    modifier: Modifier = Modifier,
    ratio: Float? = DishImageTokens.ASPECT_RATIO,
) {
    val imageModifier = (ratio?.let { modifier.aspectRatio(it) } ?: modifier).fillMaxSize()
    dish.photoLink?.let {
        DishImage(
            it,
            loadImmediately = loadImmediately,
            modifier = imageModifier,
        )
    } ?: run {
        DishImageSupplement(
            dish.name.hashCode(),
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = imageModifier,
        )
    }
}

@Composable
internal fun DishImageRatio(
    photoLink: String,
    loadImmediately: Boolean,
    modifier: Modifier = Modifier,
    ratio: Float = DishImageTokens.ASPECT_RATIO,
) {
    val imageModifier =
        modifier
            .aspectRatio(ratio)
            .fillMaxSize()

    DishImage(
        photoLink = photoLink,
        loadImmediately = loadImmediately,
        modifier = imageModifier,
    )
}

@Composable
internal fun DishImage(
    photoLink: String,
    loadImmediately: Boolean,
    modifier: Modifier = Modifier,
) {
    var retryHash by remember { mutableIntStateOf(0) }
    var userAllowed by rememberSaveable(photoLink) { mutableStateOf(false) }
    val canDownload = loadImmediately || userAllowed

    val imageRequest =
        with(Builder(LocalContext.current)) {
            diskCacheKey(photoLink)
            memoryCacheKey(photoLink)
            extras[Extras.Key("retry_hash")] = retryHash

            // if user is not on a metered network, images are going to be loaded from cache
            if (!canDownload) {
                networkCachePolicy(CachePolicy.DISABLED)
            }

            data(photoLink)
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
                            true,
                            color = MaterialTheme.colorScheme.secondary,
                            shape = MaterialTheme.shapes.medium,
                            highlight =
                                PlaceholderHighlight.fade(
                                    highlightColor = MaterialTheme.colorScheme.primary,
                                ),
                        ),
//                        .clickable { retryHash++ },
                )
            },
            error = {
                Box(
                    Modifier.clickable {
                        retryHash++
                        userAllowed = true
                    },
                    contentAlignment = Alignment.Center,
                ) {
                    if (canDownload) {
                        Icon(
                            Icons.Default.Refresh,
                            stringResource(string.today_list_image_load_failed),
                        )
                    } else {
                        Icon(
                            Icons.Default.Download,
                            stringResource(string.today_list_image_metered),
                        )
                    }
                }
            },
        )
    }
}

@Composable
internal fun DishImageSupplement(
    imageKey: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surfaceContainer,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier,
        color = color,
    ) {
        val icon =
            DishImageTokens.supplementIcons[(imageKey % DishImageTokens.supplementIcons.size).absoluteValue]
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(DishImageTokens.SUPPLEMENT_ICON_SIZE),
            )
        }
    }
}

private object DishImageTokens {
    const val ASPECT_RATIO = 4f / 3f
    val SUPPLEMENT_ICON_SIZE = 64.dp
    val supplementIcons =
        listOf(
            Icons.Default.LocalDining,
            Icons.Default.DinnerDining,
            Icons.Default.SoupKitchen,
            Icons.Default.EggAlt,
        )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DishImageSupplementPreview() =
    PreviewWrapper {
        listOf(
            MaterialTheme.colorScheme.surfaceContainerLow,
            MaterialTheme.colorScheme.surfaceContainer,
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.surfaceContainerHighest,
        ).forEach {
            Surface(color = MaterialTheme.colorScheme.primaryContainer) {
                DishImageSupplement(
                    remember { Random.nextInt() },
                    color = it,
                    modifier =
                    Modifier
                        .padding(12.dp)
                        .aspectRatio(DishImageTokens.ASPECT_RATIO),
                )
            }
        }
    }
