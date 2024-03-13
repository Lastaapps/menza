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

package cz.lastaapps.menza.features.starting.domain.usecase

import androidx.core.app.PendingIntentCompat.send
import cz.lastaapps.api.core.domain.sync.getData
import cz.lastaapps.api.core.domain.sync.sync
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.main.domain.usecase.GetRequestParamsUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.core.util.extensions.flattenSensible
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map

internal class CheckDataDownloadNeededUC(
    context: UCContext,
    private val masterRepo: MenzaRepo,
    private val getRequestParamsUC: GetRequestParamsUC,
) : UseCase(context) {
    suspend operator fun invoke() =
        getRequestParamsUC()
            .map { masterRepo.isReady(it) }
            .flattenSensible()
            .map { it.not() }
            .distinctUntilChanged()
}
