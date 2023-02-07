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

package cz.lastaapps.menza.starting.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import cz.lastaapps.menza.R
import cz.lastaapps.menza.starting.domain.model.DownloadProgress
import cz.lastaapps.menza.starting.domain.model.DownloadProgress.DONE
import cz.lastaapps.menza.starting.domain.model.DownloadProgress.INFO
import cz.lastaapps.menza.starting.domain.model.DownloadProgress.INIT
import cz.lastaapps.menza.starting.domain.model.DownloadProgress.MENZA_LIST

internal val DownloadProgress.text
    @Composable
    get() = stringResource(
        when (this) {
            INIT -> R.string.init_message_preparing
            MENZA_LIST -> R.string.init_message_menza
            INFO -> R.string.init_message_info
            DONE -> R.string.init_message_done
        }
    )
