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

package cz.lastaapps.menza.features.info.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cz.lastaapps.api.core.domain.model.Message
import cz.lastaapps.menza.R
import cz.lastaapps.menza.ui.theme.Padding
import cz.lastaapps.menza.ui.util.appCardColors
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun MessageList(
    messages: ImmutableList<Message>,
    modifier: Modifier = Modifier,
) {
    if (messages.isNotEmpty()) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Padding.Small),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.info_message_title),
                style = MaterialTheme.typography.titleLarge,
            )
            messages.forEach { message ->
                Message(message = message)
            }
        }
    }
}

@Composable
private fun Message(message: Message, modifier: Modifier = Modifier) {
    Card(
        colors = appCardColors(MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier,
    ) {
        Text(
            text = message.text,
            modifier = Modifier.padding(Padding.MidSmall),
            textAlign = TextAlign.Center,
        )
    }
}
