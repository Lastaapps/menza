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

package cz.lastaapps.api.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import arrow.core.left
import arrow.core.raise.nullable
import arrow.core.right
import cz.lastaapps.api.core.data.model.BalanceAccountTypeSett
import cz.lastaapps.api.core.data.model.LoginCredentialsSett
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.CommonError.NotLoggedIn
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.lighthousegames.logging.logging

/**
 * @author Marekkon5, rewriten by LastaApps (for better or worse)
 */
internal class AndroidWalletCredentialsProvider(
    private val context: Context,
) : WalletCredentialsProvider {
    companion object {
        /// Get encrypted shared preferences to store username & password
        private fun getSharedPreferences(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            return EncryptedSharedPreferences.create(
                context,
                // Update backup and extraction rules if changed!!!
                "balance_credentials",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

        private val log = logging()
    }

    private val notLoggedIn = NotLoggedIn.left()
    private val sharedPreferences by lazy { getSharedPreferences(context) }
    private var credentialsFlow: MutableStateFlow<Outcome<LoginCredentialsSett>>? = null

    override suspend fun store(credentials: LoginCredentialsSett) = synchronized(this) {
        log.i { "Storing new credentials for ${credentials.username}" }

        sharedPreferences.edit {
            with(credentials) {
                putString("username", username)
                putString("password", password)
                putString("type", type.name)
            }
        }

        credentialsFlow?.value = credentials.right()
    }

    override suspend fun clear() = synchronized(this) {
        log.i { "Clearing credentials" }
        sharedPreferences.edit { clear() }
        credentialsFlow?.value = notLoggedIn
    }

    override fun get(): Flow<Outcome<LoginCredentialsSett>> = synchronized(this) {
        if (credentialsFlow == null) {
            credentialsFlow = MutableStateFlow(
                nullable {
                    val username = sharedPreferences.getString("username", null).bind()
                    val password = sharedPreferences.getString("password", null).bind()
                    val typeName = sharedPreferences.getString("type", null).bind()

                    val type = BalanceAccountTypeSett.entries
                        .firstOrNull { it.name == typeName }
                        .bind()

                    log.i { "Read credentials for $username" }
                    LoginCredentialsSett(
                        username, password, type,
                    )
                }?.right() ?: let {
                    log.i { "Read empty credentials" }
                    notLoggedIn
                },
            )
        }

        credentialsFlow!!.asSharedFlow()
    }
}
