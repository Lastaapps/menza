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

package cz.lastaapps.menza.features.root.ui

import cz.lastaapps.core.ui.vm.Appearing
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.root.domain.usecase.IsAppSetUpUC
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetDarkModeUC
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first

internal class RootViewModel(
    context: VMContext,
    private val isAppSetUp: IsAppSetUpUC,
    private val getAppTheme: GetAppThemeUC,
    private val getDarkMode: GetDarkModeUC,
) : StateViewModel<RootState>(RootState(), context),
    Appearing {
    override var hasAppeared: Boolean = false

    override fun onAppeared() =
        launchVM {
            val isSetUp = isAppSetUp().first()
            val appTheme = getAppTheme().first()
            val darkMode = getDarkMode().first()

            updateState {
                copy(
                    isSetUp = isSetUp,
                    appTheme = appTheme,
                    darkMode = darkMode,
                    isReady = true,
                )
            }

            launchVM {
                isAppSetUp().collectLatest {
                    updateState { copy(isSetUp = it) }
                }
            }

            launchVM {
                getAppTheme().collectLatest {
                    updateState { copy(appTheme = it) }
                }
            }
            launchVM {
                getDarkMode().collectLatest {
                    updateState { copy(darkMode = it) }
                }
            }
        }
}

internal data class RootState(
    val isReady: Boolean = false,
    val isSetUp: Boolean = false,
    val appTheme: AppThemeType = AppThemeType.defaultTemp,
    val darkMode: DarkMode = DarkMode.System,
) : VMState
