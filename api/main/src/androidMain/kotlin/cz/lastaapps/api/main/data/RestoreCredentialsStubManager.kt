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

package cz.lastaapps.api.main.data

import android.content.Context
import android.content.MutableContextWrapper
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CreateRestoreCredentialRequest
import androidx.credentials.CreateRestoreCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetRestoreCredentialOption
import androidx.credentials.RestoreCredential
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.restorecredential.E2eeUnavailableException
import cz.lastaapps.api.core.domain.model.BalanceAccountType
import cz.lastaapps.core.util.extensions.localLogger
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import kotlin.system.measureNanoTime

/**
 * Manages client-side stubbing of the Android Restore Credentials API.
 * Encodes custom credentials into WebAuthn fields to enable serverless restoration.
 */
class RestoreCredentialsStubManager(
    private val context: Context,
) : RestoreCredentialsManager {
    private val log = localLogger()
    private val credentialManager = CredentialManager.create(context)
    private val secureRandom = SecureRandom()

    companion object {
        private const val DELIMITER = ":"
        private const val DEFAULT_TIMEOUT = 60000L
    }

    /**
     * Creates and registers a restore key using local credential payload.
     *
     * @param username User identifier
     * @param password User secret or auth token
     * @return Boolean indicating successful registration
     */
    override suspend fun saveRestoreCredentials(
        username: String,
        password: String,
        type: BalanceAccountType,
    ): Boolean {
        log.i { "Storing restoration credentials for $username" }

        val packageName = context.packageName
        val encodedUserId = encodeUsernamePassword(username, password, type)
        val registrationJson = buildRegistrationJson(packageName, username, encodedUserId)

        return try {
            executeCreateCredential(registrationJson, isCloudBackupEnabled = true)
            true
        } catch (e: Exception) {
            if (e is E2eeUnavailableException) {
                log.w { "E2EE unavailable. Retrying with cloud backup disabled." }
                try {
                    executeCreateCredential(registrationJson, isCloudBackupEnabled = false)
                    true
                } catch (retryEx: Exception) {
                    log.e(retryEx) { "Failed to create restore credential after fallback" }
                    false
                }
            } else {
                log.e(e) { "Failed to create restore credential" }
                false
            }
        }
    }

    /**
     * Retrieves stored restore credential and extracts the underlying username/password pair.
     *
     * @return Pair containing Username and Password, or null if no credential exists
     */
    override suspend fun restoreCredentials(): Triple<String, String, BalanceAccountType>? {
        log.i { "Attempting credentilas restoration" }
        val packageName = context.packageName
        val authenticationJson = buildAuthenticationJson(packageName)

        val option = GetRestoreCredentialOption(authenticationJson)
        val request = GetCredentialRequest(listOf(option))

        return try {
            val response = credentialManager.getCredential(MutableContextWrapper(context), request)
            val restoreCredential = response.credential as? RestoreCredential

            if (restoreCredential == null) {
                log.d { "Returned credential is not of type RestoreCredential" }
                return null
            }

            extractPayloadFromResponse(restoreCredential.authenticationResponseJson)
                ?.also { log.i { "Restored credentials for ${it.first}" } }
        } catch (e: NoCredentialException) {
            log.d(e) { "No restore credential available on this device" }
            null
        } catch (e: GetCredentialException) {
            log.e(e) { "Error retrieving restore credential" }
            null
        } catch (e: Exception) {
            log.e(e) { "Unexpected error during credential restoration" }
            null
        }
    }

    /**
     * Clears the restore credential state upon user logout.
     *
     * @return Boolean indicating success
     */
    override suspend fun clearRestoreCredentials(): Boolean =
        try {
            val clearRequest =
                ClearCredentialStateRequest(
                    ClearCredentialStateRequest.TYPE_CLEAR_RESTORE_CREDENTIAL,
                )
            credentialManager.clearCredentialState(clearRequest)
            true
        } catch (e: ClearCredentialException) {
            log.e(e) { "Failed to clear restore credential state" }
            false
        }

    private suspend fun executeCreateCredential(
        registrationJson: String,
        isCloudBackupEnabled: Boolean,
    ): CreateRestoreCredentialResponse {
        var res: CreateRestoreCredentialResponse
        measureNanoTime {
            val request =
                CreateRestoreCredentialRequest(
                    requestJson = registrationJson,
                    isCloudBackupEnabled = isCloudBackupEnabled,
                )
            res =
                credentialManager.createCredential(
                    MutableContextWrapper(context),
                    request,
                ) as CreateRestoreCredentialResponse
        }.let {
            log.i { "Performing 'executeCreateCredential()' took ${it / 1_000_000} ms" }
        }
        return res
    }

    private fun buildRegistrationJson(
        packageName: String,
        username: String,
        encodedUserId: String,
    ): String {
        val challengeBytes = ByteArray(32).also { secureRandom.nextBytes(it) }
        val encodedChallenge =
            Base64.encodeToString(
                challengeBytes,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
            )

        val rpObj =
            JSONObject().apply {
                put("name", packageName)
                put("id", packageName)
            }

        val userObj =
            JSONObject().apply {
                put("id", encodedUserId)
                put("name", username)
                put("displayName", username)
            }

        val pubKeyParams =
            JSONArray().apply {
                put(
                    JSONObject().apply {
                        put("type", "public-key")
                        put("alg", -7)
                    },
                )
                put(
                    JSONObject().apply {
                        put("type", "public-key")
                        put("alg", -257)
                    },
                )
            }

        val authSelection =
            JSONObject().apply {
                put("residentKey", "required")
                put("requireResidentKey", true)
                put("userVerification", "discouraged")
            }

        return JSONObject()
            .apply {
                put("rp", rpObj)
                put("user", userObj)
                put("challenge", encodedChallenge)
                put("pubKeyCredParams", pubKeyParams)
                put("timeout", DEFAULT_TIMEOUT)
                put("authenticatorSelection", authSelection)
                put("attestation", "none")
            }.toString()
    }

    private fun buildAuthenticationJson(packageName: String): String {
        val challengeBytes = ByteArray(32).also { secureRandom.nextBytes(it) }
        val encodedChallenge =
            Base64.encodeToString(
                challengeBytes,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
            )

        return JSONObject()
            .apply {
                put("challenge", encodedChallenge)
                put("timeout", DEFAULT_TIMEOUT)
                put("rpId", packageName)
                put("userVerification", "discouraged")
            }.toString()
    }

    private fun extractPayloadFromResponse(responseJson: String): Triple<String, String, BalanceAccountType>? =
        try {
            val rootObj = JSONObject(responseJson)
            val responseObj = rootObj.getJSONObject("response")
            val userHandle = responseObj.getString("userHandle")

            return decodeUsernamePassword(userHandle)
        } catch (e: Exception) {
            log.e(e) { "Failed to parse restore credential response JSON" }
            null
        }

    private fun encodeUsernamePassword(
        username: String,
        password: String,
        type: BalanceAccountType,
    ) = "${username.encodeBase64()}$DELIMITER${password.encodeBase64()}$DELIMITER${type.name.encodeBase64()}".encodeBase64()

    private fun decodeUsernamePassword(source: String): Triple<String, String, BalanceAccountType>? =
        source.decodeBase64().split(DELIMITER, limit = 3).let { parts ->
            if (parts.size == 3) {
                Triple(
                    parts[0],
                    parts[1],
                    BalanceAccountType.entries.firstOrNull { it.name == parts[2] }
                        ?: return@let null,
                )
            } else {
                log.e { "Malformed credential payload structure" }
                null
            }
        }

    private fun String.encodeBase64() =
        Base64.encodeToString(
            this.toByteArray(StandardCharsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
        )

    private fun String.decodeBase64() =
        String(
            Base64.decode(
                this,
                Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP,
            ),
            StandardCharsets.UTF_8,
        )
}
