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

package cz.lastaapps.menza.ui.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cz.lastaapps.entity.menza.MenzaId

@Composable
fun InfoAllTogether(
    menzaId: MenzaId?,
    viewModel: MenzaInfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) {
        Text(text = "No menza selected")
        return
    }
    Column() {
        val data by viewModel.getOpeningHours(menzaId).collectAsState(initial = null)
        if (data != null) {
            OpeningHoursUI(data = data!!)
        }

        InfoCommon(menzaId = menzaId, viewModel = viewModel)
    }
}

@Composable
fun InfoJustBasic(
    menzaId: MenzaId?,
    viewModel: MenzaInfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) {
        Text(text = "No menza selected")
        return
    }
    InfoCommon(menzaId = menzaId, viewModel = viewModel)
}

@Composable
fun InfoRemaining(
    menzaId: MenzaId?,
    viewModel: MenzaInfoViewModel,
    modifier: Modifier = Modifier,
) {
    if (menzaId == null) return
    val data by viewModel.getOpeningHours(menzaId).collectAsState(initial = null)
    if (data == null) return
    OpeningHoursUI(data = data!!)
}

@Composable
private fun InfoCommon(
    menzaId: MenzaId,
    viewModel: MenzaInfoViewModel,
    modifier: Modifier = Modifier,
) {
    val location by viewModel.getLocation(menzaId).collectAsState(initial = null)
    val contacts by viewModel.getContacts(menzaId).collectAsState(initial = null)
    val messages by viewModel.getMessage(menzaId).collectAsState(initial = null)

    if (location == null || contacts == null || messages == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column {
            location!!.forEach {
                Address(address = it.address)
                OpenMap(location = it.location)
            }
            contacts!!.forEach {
                Contacts(contact = it)
            }
            messages!!.forEach { Message(it) }
        }
    }
}



