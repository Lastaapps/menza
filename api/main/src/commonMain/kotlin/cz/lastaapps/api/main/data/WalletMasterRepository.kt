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

package cz.lastaapps.api.main.data

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.right
import arrow.core.rightIor
import cz.lastaapps.api.core.data.SimpleProperties
import cz.lastaapps.api.core.data.WalletCredentialsProvider
import cz.lastaapps.api.core.data.model.BalanceAccountTypeSett
import cz.lastaapps.api.core.data.model.LoginCredentialsSett
import cz.lastaapps.api.core.data.model.toDomain
import cz.lastaapps.api.core.data.model.toSett
import cz.lastaapps.api.core.domain.model.BalanceAccountType
import cz.lastaapps.api.core.domain.model.BalanceAccountType.CTU
import cz.lastaapps.api.core.domain.model.UserBalance
import cz.lastaapps.api.core.domain.sync.SyncJob
import cz.lastaapps.api.core.domain.sync.SyncOutcome
import cz.lastaapps.api.core.domain.sync.SyncProcessor
import cz.lastaapps.api.core.domain.sync.SyncResult
import cz.lastaapps.api.core.domain.sync.SyncSource
import cz.lastaapps.api.core.domain.sync.runSync
import cz.lastaapps.api.core.domain.validity.ValidityChecker
import cz.lastaapps.api.core.domain.validity.ValidityKey
import cz.lastaapps.api.core.domain.validity.withCheckRecent
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.menza.api.agata.api.AgataCtuWalletApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging

internal interface WalletMasterRepository : SyncSource<UserBalance?> {
    suspend fun login(username: String, password: String, type: BalanceAccountType): Outcome<Unit>
    suspend fun logout(): Outcome<Unit>
}

internal class WalletMasterRepositoryImpl(
    private val ctuApi: AgataCtuWalletApi,
    private val credentialsProvider: WalletCredentialsProvider,
    private val simpleProperties: SimpleProperties,
    private val processor: SyncProcessor,
    private val checker: ValidityChecker,
) : WalletMasterRepository {

    private val validityKey = ValidityKey.agataCtuBalance()
    private val scope = CoroutineScope(Dispatchers.Default)

    companion object {
        private val log = logging()
    }

    private fun selectApi(type: BalanceAccountType) = when (type) {
        CTU -> ctuApi
    }

    override suspend fun login(
        username: String,
        password: String,
        type: BalanceAccountType,
    ): Outcome<Unit> = outcome {
        log.i { "Trying to login" }

        val api = selectApi(type)
        api.getBalance(username, password).bind()

        credentialsProvider.store(LoginCredentialsSett(username, password, type.toSett()))
        log.i { "Login successful" }
    }

    override suspend fun logout(): Outcome<Unit> = outcome {
        credentialsProvider.clear()
        log.i { "Logout successful" }
    }

    override fun getData(): Flow<UserBalance?> = channelFlow {
        credentialsProvider.get().collectLatest { credentials ->
            when (credentials) {
                is Left -> send(null)
                is Right -> {
                    simpleProperties.getBalance()
                        .collectLatest { balance ->
                            send(
                                balance?.let {
                                    UserBalance(credentials.value.username, balance)
                                },
                            )
                        }
                }
            }
        }
    }.distinctUntilChanged()

    private val job = object : SyncJob<Float, Float>(
        shouldRun = { _ ->
            if (credentialsProvider.get().first()
                    .map {
                        it.type == BalanceAccountTypeSett.CTU
                    }
                    .getOrNull() == true
            ) {
                arrow.core.Some {}
            } else {
                arrow.core.None
            }
        },
        fetchApi = {
            val credentials = credentialsProvider.get().first().bind()
            val api = selectApi(credentials.type.toDomain())
            api.getBalance(credentials.username, credentials.password).bind()
        },
        convert = { it.rightIor() },
        store = { data -> scope.launch { simpleProperties.setBalance(data) } },
    ) {}

    override suspend fun sync(isForced: Boolean): SyncOutcome = run {
        log.i { "Requesting data sync" }

        credentialsProvider.get().first().let { credentials ->
            when (credentials) {
                is Left -> SyncResult.Unavailable.right()
                is Right ->
                    checker.withCheckRecent(validityKey, isForced) {
                        processor.runSync(job, listOf(), isForced = isForced)
                    }
            }
        }
    }
}
