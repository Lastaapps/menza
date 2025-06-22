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

package cz.lastaapps.core.data

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import arrow.core.Either
import arrow.core.flatten
import arrow.core.raise.Raise
import arrow.core.raise.either
import cz.lastaapps.core.data.model.CipherIV
import cz.lastaapps.core.domain.error.CommonError
import cz.lastaapps.core.util.extensions.localLogger
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Handles AES encryption and decryption using AndroidKeyStore for key management.
 *
 * This class uses AES/GCM/NoPadding, which is a recommended authenticated encryption mode.
 * GCM provides both confidentiality and authenticity.
 *
 * @param keyAlias The alias under which the encryption key will be stored in the AndroidKeyStore.
 *                 It's recommended to use a unique alias per piece of data or purpose.
 */
@RequiresApi(Build.VERSION_CODES.M)
class JavaXCryptoProvider(
    private val keyAlias: String = "shared_key_alias",
) : CryptoProvider {
    companion object {
        private const val ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val AES_MODE = KeyProperties.KEY_ALGORITHM_AES
        private const val AES_KEY_SIZE = 256
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$AES_MODE/$BLOCK_MODE/$PADDING"

        private const val GCM_TAG_LENGTH_BITS = 128

        private val log = localLogger()
    }

    private val keyStore: KeyStore =
        KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER).also {
            it.load(null)
        }

    /**
     * Retrieves the SecretKey from AndroidKeyStore.
     */
    private fun getSecretKey(): CryptoOutcome<SecretKey?> = cryptoCatch { (keyStore.getKey(keyAlias, null) as? SecretKey) }

    /**
     * Retrieves the SecretKey from AndroidKeyStore. If it doesn't exist, generates a new one.
     */
    private fun getOrCreateSecretKey(): SecretKey = (keyStore.getKey(keyAlias, null) as? SecretKey) ?: generateSecretKey()

    /**
     * Generates a new AES SecretKey and stores it in the AndroidKeyStore.
     */
    private fun generateSecretKey(): SecretKey {
        log.d { "Generating secret key" }
        val keyGenerator = KeyGenerator.getInstance(AES_MODE, ANDROID_KEYSTORE_PROVIDER)
        val specBuilder =
            KeyGenParameterSpec
                .Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                ).setBlockModes(BLOCK_MODE)
                .setEncryptionPaddings(PADDING)
                .setKeySize(AES_KEY_SIZE)

        specBuilder.setRandomizedEncryptionRequired(true)

        keyGenerator.init(specBuilder.build())
        return keyGenerator.generateKey()
    }

    /**
     * Encrypts the given ByteArray.
     *
     * @param data The ByteArray to encrypt.
     * @return A Pair containing the encrypted ciphertext and the IV.
     *         The IV is necessary for decryption and should be stored alongside the ciphertext.
     *         Returns null if encryption fails.
     */
    override fun encryptData(data: ByteArray): CryptoOutcome<Pair<ByteArray, CipherIV>> =
        cryptoCatch {
            log.d { "Encrypting data" }
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = CipherIV(cipher.iv)
            val ciphertext = cipher.doFinal(data)
            log.d { "Encrypted data with IV $iv" }
            ciphertext to iv
        }

    /**
     * Decrypts the given ciphertext ByteArray.
     *
     * @param ciphertext The ByteArray to decrypt.
     * @param iv The Initialization Vector (IV) used during encryption.
     * @return The decrypted ByteArray. Returns null if decryption fails.
     */
    override fun decryptData(
        ciphertext: ByteArray,
        iv: CipherIV,
    ): CryptoOutcome<ByteArray?> =
        cryptoCatch {
            log.d { "Decrypting data with IV $iv" }
            val secretKey = getSecretKey().bind() ?: return@cryptoCatch null
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv.value)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)
            cipher.doFinal(ciphertext)
        }

    /**
     * Deletes the key associated with this cipher's alias from the AndroidKeyStore.
     * Call this if you no longer need to decrypt data encrypted with this key.
     */
    fun deleteKey() =
        cryptoCatch {
            if (keyStore.containsAlias(keyAlias)) {
                log.d { "Deleting key $keyAlias" }
                keyStore.deleteEntry(keyAlias)
            } else {
                log.d { "Key $keyAlias does not exist" }
            }
        }

    private fun <T> cryptoCatch(block: Raise<CommonError.CryptoError>.() -> T): CryptoOutcome<T> =
        either {
            Either
                .catchOrThrow<java.security.GeneralSecurityException, T> {
                    block()
                }.onLeft {
                    log.e(it) { "Encryption error" }
                }.mapLeft { CommonError.CryptoError(it) }
        }.flatten()
}
