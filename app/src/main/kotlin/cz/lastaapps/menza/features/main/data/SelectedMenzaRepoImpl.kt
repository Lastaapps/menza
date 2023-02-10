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

package cz.lastaapps.menza.features.main.data

import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.menza.features.main.domain.SelectedMenzaRepo
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.GetInitialMenzaUC
import cz.lastaapps.menza.features.settings.domain.usecase.initialmenza.SetInitialMenzaUC
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal class SelectedMenzaRepoImpl(
    // TODO get initial menza
    private val getInitialMenza: GetInitialMenzaUC,
    private val setInitialMenza: SetInitialMenzaUC,
) : SelectedMenzaRepo {

    private var isReady = false
    private val selected = MutableStateFlow<MenzaType?>(null)

    override suspend fun getSelectedMenza(): Flow<MenzaType?> {
        if (isReady) {
            isReady = true
//             getInitialMenza().first().let { initial ->
//                 selected.update {initial}
//             }
        }
        return selected
    }

    override suspend fun selectMenza(menza: MenzaType?) {
//        setInitialMenza(menza)
        selected.update { menza }
    }
}
