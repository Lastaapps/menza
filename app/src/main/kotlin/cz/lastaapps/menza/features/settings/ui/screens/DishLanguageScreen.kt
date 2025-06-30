/*
 *    Copyright 2025, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.menza.features.settings.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.DataLanguage.Czech
import cz.lastaapps.api.core.domain.model.DataLanguage.English
import cz.lastaapps.core.ui.vm.HandleDismiss
import cz.lastaapps.menza.R
import cz.lastaapps.menza.features.settings.ui.vm.DishLanguageState
import cz.lastaapps.menza.features.settings.ui.vm.DishLanguageViewModel
import cz.lastaapps.menza.ui.theme.AppTheme
import cz.lastaapps.menza.ui.theme.Padding

@Composable
internal fun DishLanguageScreen(
    onComplete: () -> Unit,
    viewModel: DishLanguageViewModel,
    modifier: Modifier = Modifier,
) {
    DishLanguageEffects(
        viewModel,
        onSelect = onComplete,
    )

    DishLanguageContent(
        onLanguage = { viewModel.selectLanguage(it) },
        modifier = modifier,
    )
}

@Composable
private fun DishLanguageEffects(
    viewModel: DishLanguageViewModel,
    onSelect: () -> Unit,
) {
    HandleDismiss(
        viewModel,
        DishLanguageState::isSelected,
        DishLanguageViewModel::dismissSelected,
        onSelect,
    )
}

@Composable
private fun DishLanguageContent(
    onLanguage: (DataLanguage) -> Unit,
    modifier: Modifier = Modifier,
) = Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(Padding.Medium),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(
                    Padding.Medium,
                    Alignment.CenterVertically,
                ),
        ) {
            Text(
                text = stringResource(R.string.language_choose_title),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )

            Text(
                text = stringResource(R.string.language_choose_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )

            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.spacedBy(Padding.Small),
            ) {
                LanguageButton(
                    stringResource(R.string.language_choose_czech_title),
                    stringResource(R.string.language_choose_czech_description),
                    onClick = { onLanguage(Czech) },
                    modifier = Modifier.fillMaxWidth(),
                )
                LanguageButton(
                    stringResource(R.string.language_choose_english_title),
                    stringResource(R.string.language_choose_english_description),
                    onClick = { onLanguage(English) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(Padding.Medium))

            Text(
                text = stringResource(id = R.string.language_choose_disclaimer),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Composable
private fun LanguageButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) = ElevatedCard(onClick, modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Padding.Tiny),
        modifier =
            Modifier
                .padding(Padding.MidSmall)
                .fillMaxWidth(),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview
@Composable
private fun DishLanguagePreview() {
    AppTheme {
        DishLanguageContent(onLanguage = {})
    }
}
