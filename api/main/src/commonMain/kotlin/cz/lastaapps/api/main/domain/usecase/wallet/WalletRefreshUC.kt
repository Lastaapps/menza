/*
 *    Copyright 2026, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.api.main.domain.usecase.wallet

import cz.lastaapps.api.core.data.model.toDomain
import cz.lastaapps.api.main.data.RestoreCredentialsManager
import cz.lastaapps.api.main.data.WalletMasterRepository
import cz.lastaapps.core.domain.UCContext
import cz.lastaapps.core.domain.UseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class WalletRefreshUC internal constructor(
    ucContext: UCContext,
    private val repo: WalletMasterRepository,
    private val restoreCredentialsManager: RestoreCredentialsManager,
) : UseCase(ucContext) {
    suspend operator fun invoke(isForced: Boolean) =
        launch {
            coroutineScope {
                val job =
                    this.async {
                        repo.sync(Unit, isForced = isForced)
                    }

                // Restore credentials registration
                // This should run only on first launch
                // But I don't care, private key generation goes brrrr
                this.launch {
                    @Suppress("DEPRECATION")
                    val credentials = repo.getCredentials()
                    credentials?.let {
                        restoreCredentialsManager.saveRestoreCredentials(
                            it.username,
                            it.password,
                            it.type.toDomain(),
                        )
                    }
                }

                job.await()
            }
        }
}
