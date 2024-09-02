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

package cz.lastaapps.menza.features.settings.domain.usecase.initialmenza

import cz.lastaapps.api.main.domain.usecase.GetMenzaListUC
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import kotlinx.coroutines.flow.combine

internal class GetPreferredMenzaUC internal constructor(
    context: UCContext,
    private val getMenzaListUC: GetMenzaListUC,
    private val repo: MainSettingsRepo,
) : UseCase(context) {
    suspend operator fun invoke() =
        launch {
            combine(
                getMenzaListUC(),
                repo.getPreferredMenza(),
            ) { list, preferred ->
                list.firstOrNull { it.type == preferred }
            }
        }
}
