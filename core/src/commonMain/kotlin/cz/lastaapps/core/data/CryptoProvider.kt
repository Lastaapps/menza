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

import arrow.core.Either
import arrow.core.right
import cz.lastaapps.core.data.model.CipherIV
import cz.lastaapps.core.domain.error.CommonError
import java.nio.charset.StandardCharsets

typealias CryptoOutcome<T> = Either<CommonError.CryptoError, T>

interface CryptoProvider {
    fun encryptData(data: ByteArray): CryptoOutcome<Pair<ByteArray, CipherIV>>

    fun decryptData(
        ciphertext: ByteArray,
        iv: CipherIV,
    ): CryptoOutcome<ByteArray?>
}

/** Used on Android API < 23 */
class DummyCryptoProvider : CryptoProvider {
    override fun encryptData(data: ByteArray): CryptoOutcome<Pair<ByteArray, CipherIV>> = (data to CipherIV(byteArrayOf())).right()

    override fun decryptData(
        ciphertext: ByteArray,
        iv: CipherIV,
    ): CryptoOutcome<ByteArray?> = ciphertext.right()
}

/**
 * Encrypts the given plaintext string.
 *
 * @param plaintext The string to encrypt.
 * @return A Pair containing the encrypted ciphertext (as ByteArray) and the IV (as ByteArray).
 *         Returns null if encryption fails.
 */
fun CryptoProvider.encryptString(plaintext: String): CryptoOutcome<Pair<ByteArray, CipherIV>> =
    encryptData(plaintext.toByteArray(StandardCharsets.UTF_8))

/**
 * Decrypts the given ciphertext string.
 *
 * @param ciphertext The ByteArray to decrypt.
 * @param iv The Initialization Vector (IV) used during encryption.
 * @return The decrypted plaintext string. Returns null if decryption fails.
 */
fun CryptoProvider.decryptString(
    ciphertext: ByteArray,
    iv: CipherIV,
): CryptoOutcome<String?> =
    decryptData(ciphertext, iv)
        .map { it?.toString(StandardCharsets.UTF_8) }
