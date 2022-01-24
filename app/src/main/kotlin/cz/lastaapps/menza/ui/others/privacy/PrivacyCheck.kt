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

package cz.lastaapps.menza.ui.others.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun PrivacyCheck(
    privacyViewModel: PrivacyViewModel,
    onDrawReady: () -> Unit,
    content: @Composable () -> Unit,
) {
    val state by privacyViewModel.shouldShow.collectAsState()

    when (state) {
        false -> {
            content()
        }
        true -> {
            PrivacyDialog(
                shown = true,
                onDismissRequest = {},
                showAccept = true,
            ) {
                privacyViewModel.onApprove()
            }
            onDrawReady()
        }
        else -> {}
    }
}
