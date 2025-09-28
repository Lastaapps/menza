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

package cz.lastaapps.api.main.domain.usecase

import arrow.fx.coroutines.parMap
import cz.lastaapps.api.core.domain.repo.MenzaRepo
import cz.lastaapps.api.core.domain.sync.sync
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase

class SyncMenzaListUC(
    context: UCContext,
    private val menzaRepo: MenzaRepo,
    private val getRequestParamsUC: GetRequestParamsUC,
    private val getImportantRequestParams: GetImportantRequestParams,
) : UseCase(context) {
    suspend operator fun invoke(
        isForced: Boolean = false,
        allSpecs: Boolean = false,
    ) = launch {
        if (allSpecs) {
            getImportantRequestParams()
                .parMap {
                    menzaRepo.sync(it, isForced = isForced)
                }.let { list ->
                    if (list.all { it.isRight() }) {
                        list.first()
                    } else {
                        list.first { it.isLeft() }
                    }
                }
        } else {
            menzaRepo.sync(getRequestParamsUC(), isForced = isForced)
        }
    }
}
