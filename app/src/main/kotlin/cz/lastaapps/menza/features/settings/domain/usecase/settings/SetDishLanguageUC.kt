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

package cz.lastaapps.menza.features.settings.domain.usecase.settings

import arrow.core.right
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.main.domain.usecase.SyncMenzaListUC
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import kotlinx.coroutines.flow.first

class SetDishLanguageUC internal constructor(
    context: UCContext,
    private val repo: MainSettingsRepo,
    private val syncMenzaListUC: SyncMenzaListUC,
) : UseCase(context) {
    suspend operator fun invoke(language: DataLanguage): Outcome<Unit> =
        launch {
            val oldLang = repo.getDishLanguage().first()
            if (oldLang == language) return@launch Unit.right()

            repo.setDishLanguage(language)
            syncMenzaListUC(
                isForced = false,
                allSpecs = false,
            ).onLeft {
                repo.setDishLanguage(oldLang)
            }.map { }
        }
}
