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

package cz.lastaapps.menza.ui.dests.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.lastaapps.entity.menza.Message
import cz.lastaapps.menza.R

@Composable
fun MessageList(
    messages: List<Message>,
    modifier: Modifier = Modifier,
) {
    if (messages.isNotEmpty()) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                stringResource(R.string.info_message_title),
                style = MaterialTheme.typography.titleLarge
            )
            messages.forEach {
                Message(it, Modifier.fillMaxWidth())
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Message(message: Message, modifier: Modifier = Modifier) {
    Card(containerColor = MaterialTheme.colorScheme.primaryContainer, modifier = modifier) {
        Text(message.message, modifier = Modifier.padding(12.dp))
    }
}
