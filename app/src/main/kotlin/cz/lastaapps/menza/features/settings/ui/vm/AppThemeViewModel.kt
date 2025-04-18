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

package cz.lastaapps.menza.features.settings.ui.vm

import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.settings.domain.model.AppThemeType
import cz.lastaapps.menza.features.settings.domain.model.DarkMode
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetDarkModeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.GetThemeListUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.SetAppThemeUC
import cz.lastaapps.menza.features.settings.domain.usecase.theme.SetDarkModeUC
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class AppThemeViewModel(
    context: VMContext,
    private val getAppThemeList: GetThemeListUC,
    private val getAppTheme: GetAppThemeUC,
    private val getDarkMode: GetDarkModeUC,
    private val setAppTheme: SetAppThemeUC,
    private val setDarkMode: SetDarkModeUC,
) : StateViewModel<AppThemeState>(AppThemeState(), context) {
    override suspend fun onFirstAppearance() {
        getAppThemeList().let {
            updateState { copy(availableThemes = it) }
        }
    }

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        getAppTheme()
            .onEach { theme ->
                updateState { copy(theme = theme) }
            }.launchIn(scope)
        getDarkMode()
            .onEach { mode ->
                updateState { copy(darkMode = mode) }
            }.launchIn(scope)
    }

    fun setAppTheme(theme: AppThemeType) = launchVM { setAppTheme.invoke(theme) }

    fun setDarkMode(mode: DarkMode) = launchVM { setDarkMode.invoke(mode) }
}

internal data class AppThemeState(
    val theme: AppThemeType? = null,
    val darkMode: DarkMode? = null,
    val availableThemes: ImmutableList<AppThemeType> = persistentListOf(),
) : VMState
