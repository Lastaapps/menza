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

package cz.lastaapps.menza.features.settings.domain.usecase.theme

import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import cz.lastaapps.menza.features.settings.domain.MainSettingsRepo
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import kotlinx.coroutines.flow.map

class GetAppThemeUC internal constructor(
    context: UCContext,
    private val repo: MainSettingsRepo,
    private val isDynamicThemeSupported: IsDynamicThemeSupportedUC,
) : UseCase(context) {
    suspend operator fun invoke() =
        launch {
            repo.getAppTheme().map {
                it ?: run {
                    if (isDynamicThemeSupported()) {
                        AppThemeType.System
                    } else {
                        AppThemeType.Agata
                    }

                    AppThemeType.Agata
                }
            }
        }
}
