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

package cz.lastaapps.menza.starting.ui.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PrivacyDialogDest(
    onNotNeeded: suspend () -> Unit,
    privacyViewModel: PrivacyViewModel = koinViewModel(),
) {
    val state by privacyViewModel.shouldShow.collectAsState()

    when (state) {
        true ->
            PrivacyDialog(
                onDismissRequest = {},
                showAccept = true,
                onAccept = privacyViewModel::onApprove,
            )
        false ->
            LaunchedEffect(Unit) { onNotNeeded() }
        null -> {}
    }
}
