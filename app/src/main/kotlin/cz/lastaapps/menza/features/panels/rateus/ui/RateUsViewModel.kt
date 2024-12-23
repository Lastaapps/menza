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

package cz.lastaapps.menza.features.panels.rateus.ui

import androidx.compose.runtime.Composable
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.model.AppSocial
import cz.lastaapps.core.domain.usecase.OpenAppSocialUC
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.DismissRateUsUC
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.RecordAppOpenedUC
import cz.lastaapps.menza.features.panels.rateus.domain.usecase.ShouldShowRateUsUC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class RateUsViewModel(
    context: VMContext,
    private val shouldShowRateUsUC: ShouldShowRateUsUC,
    private val appOpenedUC: RecordAppOpenedUC,
    private val dismissRateUsUC: DismissRateUsUC,
    private val openSocialsUC: OpenAppSocialUC,
) : StateViewModel<RateUsViewModel.State>(State(), context),
    ErrorHolder {
    override suspend fun whileSubscribed(scope: CoroutineScope) {
        shouldShowRateUsUC()
            .onEach {
                updateState { copy(shouldShow = it) }
            }.launchIn(scope)

        scope.launch { appOpenedUC() }
    }

    fun ratePlayStore() =
        openSocialsUC(AppSocial.PLAY_STORE_APP)
            .onRight {
                updateState { copy(playRated = true) }
                dismissIfCompleted()
            }.onLeft {
                updateState { copy(error = it) }
            }.let {}

    fun rateGithub() =
        openSocialsUC(AppSocial.GITHUB_REPO)
            .onRight {
                updateState { copy(githubRated = true) }
                dismissIfCompleted()
            }.onLeft {
                updateState { copy(error = it) }
            }.let {}

    fun later() =
        launchVM {
            dismissRateUsUC(permanent = false)
        }

    fun dismiss() =
        launchVM {
            dismissRateUsUC(permanent = true)
        }

    private fun dismissIfCompleted() =
        lastState().let { state ->
            if (state.playRated && state.githubRated) {
                launchVM {
                    dismissRateUsUC(permanent = true)
                }
            }
        }

    data class State(
        val shouldShow: Boolean = false,
        val playRated: Boolean = false,
        val githubRated: Boolean = false,
        val error: DomainError? = null,
    ) : VMState

    override fun dismissError() = updateState { copy(error = null) }

    @Composable
    override fun getError(): DomainError? = flowState.value.error
}
