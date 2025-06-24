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

package cz.lastaapps.api.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.flatten
import arrow.core.left
import arrow.core.raise.either
import arrow.core.raise.nullable
import arrow.core.right
import arrow.core.some
import cz.lastaapps.api.core.data.model.BalanceAccountTypeSett
import cz.lastaapps.api.core.data.model.LoginCredentialsSett
import cz.lastaapps.core.data.CryptoProvider
import cz.lastaapps.core.data.crypto.getEncryptedString
import cz.lastaapps.core.data.crypto.putEncryptedString
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.CommonError.NotLoggedIn
import cz.lastaapps.core.util.extensions.localLogger
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

/**
 * @author Marekkon5, rewriten by LastaApps (for better or worse)
 */
internal class AndroidWalletCredentialsProvider(
    private val context: Context,
    private val cryptoProvider: CryptoProvider,
) : WalletCredentialsProvider {
    companion object {
        // Migration logic
        fun sharedPreferencesFileExists(
            context: Context,
            preferenceName: String,
        ): Boolean {
            val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            val prefsFile = File(prefsDir, "$preferenceName.xml")
            return prefsFile.exists() && prefsFile.isFile
        }

        fun deleteSharedPreferencesFile(
            context: Context,
            preferenceName: String,
        ) {
            val prefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
            val prefsFile = File(prefsDir, "$preferenceName.xml")
            prefsFile.delete()
        }

        private val notLoggedIn = NotLoggedIn.left()
    }

    private val log = localLogger()

    private var sharedPreferences: Option<SharedPreferences> = None
    private var credentialsFlow: MutableStateFlow<Outcome<LoginCredentialsSett>>? = null

    private fun getSharedPreferences(): SharedPreferences =
        synchronized(this) {
            when (val sp = sharedPreferences) {
                is Some -> sp.value
                is None ->
                    context
                        .getSharedPreferences(
                            // Update backup and extraction rules if changed!!!
                            "balance_credentials_v02",
                            Context.MODE_PRIVATE,
                        ).also {
                            sharedPreferences = it.some()
                            migrateOldEncryptedSharedPreferences(context, it)
                        }
            }
        }

    override suspend fun store(credentials: LoginCredentialsSett) {
        synchronized(this) {
            log.i { "Storing new credentials for ***${credentials.username.takeLast(3)}" }

            getSharedPreferences().edit {
                with(cryptoProvider) {
                    with(credentials) {
                        putEncryptedString("username", username)
                        putEncryptedString("password", password)
                        putEncryptedString("type", type.name)
                    }
                }
            }

            credentialsFlow?.value = credentials.right()
        }
    }

    override suspend fun clear() =
        synchronized(this) {
            log.i { "Clearing credentials" }
            getSharedPreferences().edit { clear() }
            credentialsFlow?.value = notLoggedIn
        }

    override fun get(): Flow<Outcome<LoginCredentialsSett>> =
        synchronized(this) {
            if (credentialsFlow != null) {
                return@synchronized credentialsFlow!!.asSharedFlow()
            }

            with(cryptoProvider) {
                either {
                    val sp = getSharedPreferences()
                    val username = sp.getEncryptedString("username").bind()
                    val password = sp.getEncryptedString("password").bind()
                    val typeName = sp.getEncryptedString("type").bind()

                    val credentials =
                        nullable {
                            val type =
                                BalanceAccountTypeSett.entries
                                    .firstOrNull { it.name == typeName }
                                    .bind()

                            log.i { "Read credentials for $username" }
                            LoginCredentialsSett(
                                username.bind(),
                                password.bind(),
                                type.bind(),
                            )
                        }
                    credentials?.right() ?: let {
                        log.i { "Read empty credentials" }
                        notLoggedIn
                    }
                }.flatten()
            }.let {
                credentialsFlow = MutableStateFlow(it)
            }

            credentialsFlow!!.asSharedFlow()
        }

    // TODO delete after October 2025
    @Suppress("DEPRECATION")
    private fun migrateOldEncryptedSharedPreferences(
        context: Context,
        destSP: SharedPreferences,
    ) {
        val oldPrefFileName = "balance_credentials"
        if (!sharedPreferencesFileExists(context, oldPrefFileName)) {
            log.d { "No migration needed" }
            return
        }
        log.i { "Starting migration" }

        val masterKey =
            androidx.security.crypto.MasterKey
                .Builder(context)
                .setKeyScheme(androidx.security.crypto.MasterKey.KeyScheme.AES256_GCM)
                .build()
        val oldSP =
            androidx.security.crypto.EncryptedSharedPreferences.create(
                context,
                oldPrefFileName,
                masterKey,
                androidx.security.crypto.EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                androidx.security.crypto.EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )

        nullable {
            val username = oldSP.getString("username", null).bind()
            val password = oldSP.getString("password", null).bind()
            val typeName = oldSP.getString("type", null).bind()
            val type =
                BalanceAccountTypeSett.entries
                    .firstOrNull { it.name == typeName }
                    .bind()

            log.i { "Migrating user $username" }
            destSP.edit {
                with(cryptoProvider) {
                    putEncryptedString("username", username)
                    putEncryptedString("password", password)
                    putEncryptedString("type", type.name)
                }
            }
        }.let {
            if (it == null) {
                log.i { "Nothing to migrate" }
            }
        }

        log.i { "Migration done" }
        deleteSharedPreferencesFile(context, oldPrefFileName)
    }
}
