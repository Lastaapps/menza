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

package cz.lastaapps.core.data.crypto

import android.content.SharedPreferences
import arrow.core.raise.either
import cz.lastaapps.core.data.CryptoProvider
import cz.lastaapps.core.data.decryptString
import cz.lastaapps.core.data.encryptString
import cz.lastaapps.core.data.model.CipherIV
import cz.lastaapps.core.domain.Outcome
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val IV_KEY_SUFFIX = "_iv"

context(CryptoProvider)
fun SharedPreferences.Editor.putEncryptedString(
    key: String,
    value: String,
): Outcome<SharedPreferences.Editor> =
    either {
        val (ciphertext, iv) = encryptString(value).bind()
        putByteArray(key, ciphertext)
        putByteArray(key + IV_KEY_SUFFIX, iv.value)
    }

context(CryptoProvider)
fun SharedPreferences.getEncryptedString(key: String): Outcome<String?> =
    either {
        val ciphertext = getByteArray(key) ?: return@either null
        val iv = getByteArray(key + IV_KEY_SUFFIX) ?: return@either null
        decryptString(ciphertext, CipherIV(iv)).bind()
    }

fun SharedPreferences.Editor.removeEncryptedString(key: String): SharedPreferences.Editor = remove(key).remove(key + IV_KEY_SUFFIX)

@OptIn(ExperimentalEncodingApi::class)
fun SharedPreferences.Editor.putByteArray(
    key: String,
    value: ByteArray,
): SharedPreferences.Editor = putString(key, Base64.encode(value))

@OptIn(ExperimentalEncodingApi::class)
fun SharedPreferences.getByteArray(
    key: String,
    default: ByteArray? = null,
): ByteArray? =
    getString(key, default?.toHexString())
        ?.let { Base64.decode(it) }
