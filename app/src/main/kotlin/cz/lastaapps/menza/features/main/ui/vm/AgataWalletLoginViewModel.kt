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

package cz.lastaapps.menza.features.main.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import cz.lastaapps.api.core.domain.model.BalanceAccountType
import cz.lastaapps.api.main.domain.usecase.wallet.WalletLoginUC
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.providers.LinkOpener

internal class AgataWalletLoginViewModel(
    vmContext: VMContext,
    private val walletLoginUC: WalletLoginUC,
    private val linkOpener: LinkOpener,
) : StateViewModel<AgataWalletLoginState>(AgataWalletLoginState(), vmContext),
    ErrorHolder {
    fun logIn(method: BalanceAccountType) =
        launchVM {
            withLoading({ copy(isLoading = it) }) { state ->
                if (!state.enabled) {
                    return@withLoading
                }

                val username = state.username.trim()
                val password = state.password.trim()

                when (val res = walletLoginUC(username, password, method)) {
                    is Left -> updateState { copy(error = res.value) }
                    is Right -> updateState { copy(loginDone = true) }
                }
            }
        }

    fun setUsername(username: String) {
        updateState { copy(username = username) }
    }

    fun setPassword(password: String) {
        updateState { copy(password = password) }
    }

    fun dismissLoginDone() {
        updateState { AgataWalletLoginState() }
    }

    fun setup() {
        linkOpener.openLink("https://github.com/Lastaapps/menza/blob/main/docs/STRAVNIK_SIGNUP.md")
    }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() {
        updateState { copy(error = null) }
    }
}

internal data class AgataWalletLoginState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
    val error: DomainError? = null,
    val loginDone: Boolean = false,
) : VMState {
    val enabled: Boolean =
        username.isNotBlank() && password.isNotBlank()
}
