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
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import cz.lastaapps.api.core.domain.model.Dish
import cz.lastaapps.api.core.domain.model.DishCategory
import cz.lastaapps.core.util.extensions.mapIf
import cz.lastaapps.menza.features.today.domain.model.TodayUserSettings
import cz.lastaapps.menza.ui.components.NoItems
import cz.lastaapps.menza.ui.components.PullToRefreshWrapper
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun TodayDishCarousel(
    isLoading: Boolean,
    onRefresh: () -> Unit,
    data: ImmutableList<DishCategory>,
    onNoItems: () -> Unit,
    onDishSelected: (Dish) -> Unit,
    userSettings: TodayUserSettings,
    isOnMetered: Boolean,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    scroll: LazyListState,
    modifier: Modifier = Modifier,
) {
    PullToRefreshWrapper(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxWidth(),
    ) {
        Surface(shape = MaterialTheme.shapes.large) {
            DishContent(
                data = data,
                onDishSelected = onDishSelected,
                onNoItems = onNoItems,
                appSettings = userSettings,
                isOnMetered = isOnMetered,
                scroll = scroll,
                header = header,
                footer = footer,
                modifier = Modifier
                    .padding(top = Padding.Smaller) // so text is not cut off
                    .fillMaxSize(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishContent(
    data: ImmutableList<DishCategory>,
    onDishSelected: (Dish) -> Unit,
    onNoItems: () -> Unit,
    appSettings: TodayUserSettings,
    isOnMetered: Boolean,
    scroll: LazyListState,
    header: @Composable (Modifier) -> Unit,
    footer: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {

    //no data handling
    if (data.isEmpty()) {
        NoItems(modifier, onNoItems)
        return
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val preferredItemSize = min(maxWidth * 7 / 10, (196 + 32).dp)

        // showing items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Padding.MidSmall),
            state = scroll,
        ) {
            item(key = "header") {
                header(Modifier.animateItem())
            }

            data.forEach { category ->
                item(key = category.name + "_cat_header") {
                    DishHeader(
                        courseType = category,
                        modifier = Modifier.padding(bottom = Padding.Smaller),
                    )
                }
                item(key = category.name + "_content") {

                    if (category.dishList.size == 1) {
                        val dish = category.dishList.first()
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            DishItem(
                                dish = dish,
                                onDishSelected = onDishSelected,
                                appSettings = appSettings,
                                isOnMetered = isOnMetered,
                                modifier = Modifier
                                    .height(preferredItemSize),
                            )
                        }
                        return@item
                    }

                    val carouselState = rememberCarouselState { category.dishList.size }
                    HorizontalMultiBrowseCarousel(
                        state = carouselState,
                        preferredItemWidth = preferredItemSize,
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem(),
                        minSmallItemWidth = 64.dp,
                        maxSmallItemWidth = 128.dp,
                        itemSpacing = Padding.MidSmall,
                    ) { index ->
                        val dish = category.dishList[index]

                        // outside so the computation is run one only
                        // even if progress is called multiple times
                        val progress by remember {
                            derivedStateOf {
                                carouselItemInfo.let {
                                    // breakpoints
                                    val visible = 0.9f
                                    val hidden = 0.5f

                                    when (val ratio = it.size / it.maxSize) {
                                        in visible..1f -> 1f
                                        in hidden..visible -> (ratio - hidden) / (visible - hidden)
                                        in 0.0f..hidden -> 0f
                                        else -> 1f
                                    }.coerceAtLeast(0f)
                                }
                            }
                        }

                        DishItem(
                            dish = dish,
                            onDishSelected = onDishSelected,
                            appSettings = appSettings,
                            isOnMetered = isOnMetered,
                            modifier = Modifier
                                .height(preferredItemSize)
                                .maskClip(MaterialTheme.shapes.extraLarge),
                            progress = { progress },
                        )
                    }
                }
                item(key = category.name + "_spacer") {
                    Spacer(Modifier.height(Padding.Small))
                }
            }

            item(key = "footer") {
                footer(Modifier.animateItem())
            }
        }
    }
}

@Composable
private fun DishItem(
    dish: Dish,
    onDishSelected: (Dish) -> Unit,
    appSettings: TodayUserSettings,
    isOnMetered: Boolean,
    modifier: Modifier = Modifier,
    progress: () -> Float = { 1f },
) {
    val componentsGraphics: GraphicsLayerScope.(Alignment.Vertical) -> Unit = { alignment ->
        val translationFactor = when (alignment) {
            Alignment.Top -> -1f
            Alignment.Bottom -> 1f
            else -> error("Not supported")
        }
        val prog = progress()
        // hides other surface components when they are not in foreground
        alpha = prog
        translationY = 56.dp.toPx() * (1f - prog) * translationFactor
        scaleX = prog / 2f + 0.5f
        scaleY = prog / 2f + 0.5f
    }

    Box(
        modifier = modifier.clickable { onDishSelected(dish) },
    ) {
        DishImageOrSupplement(
            dish,
            loadImmediately = loadImmediately(appSettings.downloadOnMetered, isOnMetered),
            ratio = null,
            modifier = modifier.fillMaxSize(),
        )

        val useGradient = dish.photoLink != null
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .graphicsLayer { componentsGraphics(Alignment.Bottom) },
            verticalArrangement = Arrangement.spacedBy(Padding.Small * -1),
        ) {
            DishBadge(
                dish, priceType = appSettings.priceType,
                modifier = Modifier
                    .zIndex(2f)
                    .align(Alignment.End)
                    .padding(horizontal = Padding.MidSmall),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .mapIf(useGradient) { it.gradient() },
            ) {
                // used to reset the marquee effect
                val isVisible by remember { derivedStateOf { progress() > 0.2f } }
                if (!isVisible) {
                    @Suppress("LABEL_NAME_CLASH")
                    return@Box
                }

                Text(
                    dish.name,
                    modifier = Modifier
                        .padding(Padding.MidSmall)
                        .basicMarquee(
                            initialDelayMillis = 500,
                            iterations = Int.MAX_VALUE,
                        ),
                    maxLines = 1,
                    color = TodayDishCarouselTokens.gradientForeground.takeIf { useGradient }
                        ?: Color.Unspecified,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }

        remember(dish.amount, dish.allergens) {
            listOfNotNull(
                dish.amount,
                dish.allergens
                    ?.takeIf { it.isNotEmpty() }
                    ?.joinToString(","),
            ).joinToString(" • ").takeUnless { it.isBlank() }
        }?.let { text ->
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(Padding.MidSmall)
                    .graphicsLayer { componentsGraphics(Alignment.Top) },
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(Padding.Small),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

private object TodayDishCarouselTokens {
    val gradientBackground = Color.DarkGray
    val gradientForeground = Color.White
}

private fun Modifier.gradient(color: Color = TodayDishCarouselTokens.gradientBackground) =
    background(
        Brush.verticalGradient(
            0.0f to Color.Transparent,
            0.3f to color.copy(alpha = 0.5f),
            0.7f to color.copy(alpha = 0.8f),
            1.0f to color,
        ),
    )

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GradientTesting() = PreviewWrapper {
    Box(
        Modifier
            .background(Color.Magenta)
            .gradient(MaterialTheme.colorScheme.primary)
            .size(100.dp, 50.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("Example", color = Color.White)
    }
}