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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Min
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cz.lastaapps.api.core.domain.model.DishOriginDescriptor
import cz.lastaapps.api.core.domain.model.dish.Dish
import cz.lastaapps.api.core.domain.model.rating.RatingCategory
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.text
import cz.lastaapps.core.ui.vm.HandleDismiss
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.today.ui.util.toText
import cz.lastaapps.menza.features.today.ui.vm.RateDishViewModel
import cz.lastaapps.menza.features.today.ui.vm.RatingState
import cz.lastaapps.menza.ui.theme.MenzaColors
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.PreviewWrapper
import kotlinx.collections.immutable.toPersistentMap

@Composable
internal fun RateDishScreen(
    viewModel: RateDishViewModel,
    dish: DishOriginDescriptor,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    RateDishEffects(viewModel, onSubmit)

    val state by viewModel.flowState
    LaunchedEffect(state.isSubmitted) {
        if (state.isSubmitted) {
            viewModel.dismissDone()
        }
    }

    RateDishContent(
        state = state,
        dish = dish,
        onStar = viewModel::onStar,
        onSubmit = viewModel::submit,
        modifier = modifier,
    )
}

@Composable
private fun RateDishEffects(
    viewModel: RateDishViewModel,
    onSubmit: () -> Unit,
) {
//    HandleAppear(viewModel)
//    HandleError(viewModel, hostState)
    HandleDismiss(
        viewModel,
        RatingState::isSubmitted,
        RateDishViewModel::dismissDone,
        onSubmit,
    )
}

@Composable
private fun RateDishContent(
    state: RatingState,
    dish: DishOriginDescriptor,
    onStar: (RatingCategory, Int) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .width(intrinsicSize = Min)
                .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.Small),
    ) {
        Text(
            stringResource(R.string.rating_dialog_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
        Text(
            dish.name,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        state.rating.forEach { (category, stars) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.padding(Padding.Smaller),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        category.toText(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                    StarRow(stars, { onStar(category, it) })
                }
            }
        }
        AnimatedVisibility(
            state.error != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            state.error?.let { error ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(
                        error.text(),
                        modifier = Modifier.padding(Padding.Small),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Crossfade(state.submitting, label = "submitting") { submitting ->
            Box(modifier = Modifier.fillMaxWidth()) {
                if (submitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    Crossfade(
                        state.isValid,
                        label = "is_valid",
                        modifier = Modifier.fillMaxWidth(),
                    ) { isValid ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onSubmit,
                                enabled = isValid,
                                modifier = Modifier.align(Alignment.Center),
                            ) {
                                Text(stringResource(R.string.rating_dialog_submit))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StarRow(
    selectedStarts: Int,
    onStar: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedStarsAnimated by animateIntAsState(selectedStarts, label = "slow_switch")
    Row(
        modifier = modifier,
    ) {
        repeat(5) { i ->
            Star(
                isSelected = i < selectedStarsAnimated,
                starNo = i + 1,
                onClick = { onStar(i + 1) },
            )
        }
    }
}

@Composable
private fun Star(
    isSelected: Boolean,
    starNo: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 32.dp,
) {
    IconButton(onClick, modifier = modifier) {
        val rotation =
            Modifier.rotate(
                animateFloatAsState(
                    if (isSelected) 0f else -360f,
                    label = "rotation",
                ).value,
            )
        Crossfade(
            isSelected,
            label = "cross_fade",
        ) { target ->
            val starModifier =
                Modifier
                    .size(starSize)
                    .then(rotation)
            if (target) {
                Icon(
                    Icons.Default.StarRate,
                    stringResource(
                        R.string.rating_dialog_content_description_star_selected,
                        starNo,
                    ),
                    tint = MenzaColors.gold,
                    modifier = starModifier,
                )
            } else {
                Icon(
                    Icons.Default.StarBorder,
                    stringResource(
                        R.string.rating_dialog_content_description_star_deselected,
                        starNo,
                    ),
                    modifier = starModifier,
                )
            }
        }
    }
}

@Preview
@Composable
private fun RateDishContentPreview() =
    PreviewWrapper {
        RateDishContent(
            state =
                RatingState(
                    rating =
                        RatingCategory.entries
                            .mapIndexed { index, value -> value to index + 2 }
                            .toMap()
                            .toPersistentMap(),
                ),
            dish = Dish.Mock.dishKunda.let(DishOriginDescriptor::from),
            onStar = { _, _ -> },
            onSubmit = {},
        )
    }

@Preview
@Composable
private fun RateDishContentEmptyPreview() =
    PreviewWrapper {
        RateDishContent(
            state = RatingState(),
            dish = Dish.Mock.dishKunda.let(DishOriginDescriptor::from),
            onStar = { _, _ -> },
            onSubmit = {},
        )
    }

@Preview
@Composable
private fun RateDishContentSubmittingPreview() =
    PreviewWrapper {
        RateDishContent(
            state = RatingState(submitting = true),
            dish = Dish.Mock.dishKunda.let(DishOriginDescriptor::from),
            onStar = { _, _ -> },
            onSubmit = {},
        )
    }

@Preview
@Composable
private fun RateDishContentErrorPreview() =
    PreviewWrapper {
        RateDishContent(
            state = RatingState(error = DomainError.Unknown(Throwable("Lorem ipsum dolor sit amet"))),
            dish = Dish.Mock.dishKunda.let(DishOriginDescriptor::from),
            onStar = { _, _ -> },
            onSubmit = {},
        )
    }
