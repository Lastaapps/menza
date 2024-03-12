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

package cz.lastaapps.api.main.data

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import cz.lastaapps.api.core.data.SimpleProperties
import cz.lastaapps.api.core.data.WalletCredentialsProvider
import cz.lastaapps.api.core.data.model.LoginCredentialsSett
import cz.lastaapps.api.core.data.model.toDomain
import cz.lastaapps.api.core.data.model.toSett
import cz.lastaapps.api.core.domain.model.BalanceAccountType
import cz.lastaapps.api.core.domain.model.BalanceAccountType.CTU
import cz.lastaapps.api.core.domain.model.BalanceAccountType.Stravnik
import cz.lastaapps.api.core.domain.model.UserBalance
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.SyncSource
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.extensions.localLogger
import cz.lastaapps.menza.api.agata.api.AgataCtuWalletApi
import cz.lastaapps.menza.api.agata.api.StravnikWalletApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first


internal interface WalletMasterRepository : SyncSource<UserBalance?, Unit> {
    suspend fun login(username: String, password: String, type: BalanceAccountType): Outcome<Unit>
    suspend fun logout(): Outcome<Unit>
}

internal class WalletMasterRepositoryImpl(
    private val ctuApi: AgataCtuWalletApi,
    private val stravnikApi: StravnikWalletApi,
    private val credentialsProvider: WalletCredentialsProvider,
    private val simpleProperties: SimpleProperties,
    private val checker: ValidityChecker,
) : WalletMasterRepository {

    private val log = localLogger()

    private val validityKey = ValidityKey.agataCtuBalance()

    private fun selectApi(type: BalanceAccountType) = when (type) {
        CTU -> ctuApi
        Stravnik -> stravnikApi
    }

    override suspend fun login(
        username: String,
        password: String,
        type: BalanceAccountType,
    ): Outcome<Unit> = outcome {
        log.i { "Trying to login" }
        checker.invalidateKey(validityKey)

        val api = selectApi(type)
        api.getBalance(username, password).bind()

        credentialsProvider.store(LoginCredentialsSett(username, password, type.toSett()))
        log.i { "Login successful" }
    }

    override suspend fun logout(): Outcome<Unit> = outcome {
        checker.invalidateKey(validityKey)
        credentialsProvider.clear()
        simpleProperties.setBalance(null)
        log.i { "Logout successful" }
    }

    override fun getData(params: Unit): Flow<UserBalance?> = channelFlow {
        credentialsProvider.get().collectLatest { credentials ->
            when (credentials) {
                is Left -> send(null)
                is Right -> {
                    simpleProperties.getBalance()
                        .collectLatest { balance ->
                            send(
                                balance?.let {
                                    UserBalance(
                                        credentials.value.username,
                                        balance,
                                        credentials.value.type.toDomain(),
                                    )
                                },
                            )
                        }
                }
            }
        }
    }.distinctUntilChanged()

    private suspend fun syncImpl(): SyncOutcome = outcome {
        log.d { "Syncing data" }
        val credentials = credentialsProvider.get().first().bind()
        val api = selectApi(credentials.type.toDomain())
        val data = api.getBalance(credentials.username, credentials.password).bind()
        simpleProperties.setBalance(data)

        SyncResult.Updated
    }

    override suspend fun sync(params: Unit, isForced: Boolean): SyncOutcome = run {
        log.i { "Requesting data sync" }

        credentialsProvider.get().first().let { credentials ->
            when (credentials) {
                is Left -> SyncResult.Unavailable.right()
                is Right ->
                    checker.withCheckRecent(validityKey, isForced) {
                        syncImpl()
                    }
            }
        }
    }
}
