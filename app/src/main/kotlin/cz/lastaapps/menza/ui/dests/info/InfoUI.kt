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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cz.lastaapps.entity.menza.MenzaId
import cz.lastaapps.menza.ui.layout.menza.MenzaNotSelected

@Composable
fun InfoAllTogether(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) {
        MenzaNotSelected(navController, modifier)
        return
    }

    val location by viewModel.getLocation(menzaId).collectAsState(initial = null)
    val contacts by viewModel.getContacts(menzaId).collectAsState(initial = null)
    val messages by viewModel.getMessage(menzaId).collectAsState(initial = null)
    val openingHours by viewModel.getOpeningHours(menzaId).collectAsState(initial = null)

    if (!(location == null || contacts == null || messages == null || openingHours == null)) {
        val fillModifier = Modifier.fillMaxWidth()
        Box(modifier = modifier) {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OpeningHoursList(data = openingHours!!, fillModifier)
                ContactList(contact = contacts!!, fillModifier)
                AddressList(locations = location!!, fillModifier)
                MessageList(messages = messages!!)
            }
        }
    }
}

@Composable
fun InfoPrimary(
    navController: NavController,
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) {
        MenzaNotSelected(navController, modifier)
        return
    }

    val location by viewModel.getLocation(menzaId).collectAsState(initial = null)
    val contacts by viewModel.getContacts(menzaId).collectAsState(initial = null)
    val messages by viewModel.getMessage(menzaId).collectAsState(initial = null)
    val openingHours by viewModel.getOpeningHours(menzaId).collectAsState(initial = null)

    if (!(location == null || contacts == null || messages == null || openingHours == null)) {
        val fillModifier = Modifier.fillMaxWidth()
        Box(modifier = modifier) {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ContactList(contact = contacts!!, fillModifier)
                MessageList(messages = messages!!)
            }
        }
    }
}

@Composable
fun InfoSecondary(
    menzaId: MenzaId?,
    viewModel: InfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) return

    val location by viewModel.getLocation(menzaId).collectAsState(initial = null)
    val contacts by viewModel.getContacts(menzaId).collectAsState(initial = null)
    val messages by viewModel.getMessage(menzaId).collectAsState(initial = null)
    val openingHours by viewModel.getOpeningHours(menzaId).collectAsState(initial = null)

    if (!(location == null || contacts == null || messages == null || openingHours == null)) {
        val fillModifier = Modifier.fillMaxWidth()
        Box(modifier = modifier) {
            Column(
                Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OpeningHoursList(data = openingHours!!, fillModifier)
                AddressList(locations = location!!, fillModifier)
            }
        }
    }
}



