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

package cz.lastaapps.menza.features.main.ui.vm

import androidx.compose.runtime.Composable
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import cz.lastaapps.api.core.domain.model.UserBalance
import cz.lastaapps.api.main.domain.usecase.wallet.WalletGetBalanceUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletLogoutUC
import cz.lastaapps.api.main.domain.usecase.wallet.WalletRefreshUC
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.ui.vm.ErrorHolder
import cz.lastaapps.core.ui.vm.StateViewModel
import cz.lastaapps.core.ui.vm.VMContext
import cz.lastaapps.core.ui.vm.VMState
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.core.util.providers.LinkOpener
import cz.lastaapps.menza.features.main.domain.usecase.GetAddMoneyUrlUC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest

internal class AgataWalletViewModel(
    vmContext: VMContext,
    private val walletGetBalanceUC: WalletGetBalanceUC,
    private val walletRefreshUC: WalletRefreshUC,
    private val walletLogoutUC: WalletLogoutUC,
    private val getAddMoneyUrlUC: GetAddMoneyUrlUC,
    private val openLink: LinkOpener,
) : StateViewModel<AgataWalletState>(AgataWalletState(), vmContext),
    ErrorHolder {
    private val log = localLogger()

    override suspend fun onFirstAppearance() {
        launchVM { load(false) }
    }

    override suspend fun whileSubscribed(scope: CoroutineScope) {
        walletGetBalanceUC()
            .mapLatest { balance ->
                log.i { "New balance: $balance" }
                processBalance(balance)
            }.launchIn(scope)
    }

    private fun processBalance(balance: UserBalance?) =
        if (balance != null) {
            updateState { copy(balance = Some(balance)) }
            true
        } else {
            updateState { copy(balance = Some(null)) }
            false
        }

    fun logout() =
        launchVM {
            walletLogoutUC()
        }

    fun refresh() =
        launchVM {
            load(true)
        }

    private suspend fun load(force: Boolean) {
        withLoading({ copy(isLoading = it) }) {
            log.d { "Starting refresh: force=$force" }

            when (val res = walletRefreshUC(isForced = force)) {
                is Right -> updateState { copy(isWarning = false) }
                is Left -> updateState { copy(isWarning = true, error = res.value) }
            }

            log.d { "Refresh done" }
        }
    }

    fun onOpenWeb() {
        val type = lastState().balance.getOrNull()?.type ?: return

        openLink
            .openLink(getAddMoneyUrlUC(type))
            .onLeft { updateState { copy(error = it) } }
    }

    @Composable
    override fun getError(): DomainError? = flowState.value.error

    override fun dismissError() {
        updateState { copy(error = null) }
    }
}

internal data class AgataWalletState(
    val error: DomainError? = null,
    val isLoading: Boolean = false,
    val isWarning: Boolean = false,
    val balance: Option<UserBalance?> = None,
) : VMState
