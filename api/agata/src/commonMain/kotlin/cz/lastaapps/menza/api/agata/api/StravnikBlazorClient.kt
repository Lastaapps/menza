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

package cz.lastaapps.menza.api.agata.api

import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.raise.Raise
import arrow.core.raise.context.bind
import arrow.core.raise.context.raise
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.WalletError.TotallyBroken
import cz.lastaapps.core.domain.error.ApiError.WalletError.Unavailable
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.extensions.catchingNetwork
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.time.Duration.Companion.milliseconds

internal class StravnikBlazorClient(
    private val client: HttpClient,
) {
    private val domain = "https://stravnik.suz.cvut.cz"
    private val negotiateUrl = "$domain/_blazor/negotiate?negotiateVersion=1"
    private val transactionsUrl = "$domain/Transactions/NoSSO"

    suspend fun fetchViaSignalR(descriptor: String): Outcome<Float> =
        outcome {
            val blazorUrl = negotiate()
            handshake(blazorUrl)
            startCircuit(blazorUrl, descriptor)
            delay(200.milliseconds)

            pollForBalance(blazorUrl)
        }

    context(_: Raise<DomainError>)
    private suspend fun negotiate(): String {
        // 4. Negotiate — get a LongPolling connection token
        val negotiateJson =
            catchingNetwork {
                client.post(negotiateUrl).bodyAsText()
            }.bind()
        negotiateJson.let(::println)
        val connectionToken = getConnectionToken(negotiateJson)

        return "$domain/_blazor?id=$connectionToken"
    }

    context(_: Raise<DomainError>)
    private suspend fun handshake(blazorUrl: String) {
        // 5. Handshake: plain JSON terminated by ASCII record-separator 0x1e
        catchingNetwork {
            client
                .post(blazorUrl) {
//                        headers { //                            this.append("transport", "LongPolling") //                        }
                    setBody("{\"protocol\":\"blazorpack\",\"version\":1}\u001E")
                }.bodyAsText()
                .replace("\u001E", "001E")
                .let(::println)
        }.bind()
    }

    context(_: Raise<DomainError>)
    private suspend fun startCircuit(
        blazorUrl: String,
        descriptor: String,
    ) {
        // 6. StartCircuit: MsgPack invocation, varint-length-prefixed
        val componentsJson = """[{"type":"server","sequence":0,"descriptor":"$descriptor"}]"""
        val startCircuitFrame =
            frame(
                blazorInvocation(
                    invocationId = "1",
                    target = "StartCircuit",
                    args = listOf("$domain/", transactionsUrl, componentsJson, ""),
                ),
            )
        catchingNetwork {
            client.post(blazorUrl) {
                headers { append("Content-Type", "application/octet-stream") }
                setBody(startCircuitFrame)
            }
        }.bind()
            .bodyAsText()
            .let(::println)
    }

    context(_: Raise<DomainError>)
    private suspend fun pollForBalance(blazorUrl: String): Float {
        var handshakeChecked = false
        // 7. Long-poll loop — balance appears in an early render-batch frame as embedded HTML text
        repeat(8) {
            val res = catchingNetwork { client.get(blazorUrl).bodyAsBytes() }
            val bytes =
                when (res) {
                    is Left -> {
                        delay(200L.milliseconds)
                        return@repeat
                    }

                    is Right -> {
                        res.value
                    }
                }
            if (bytes.isNotEmpty()) {
                println(
                    bytes
                        .decodeToString(throwOnInvalidSequence = false)
                        .lineSequence()
                        .take(5)
                        .toList(),
                )
                var offset = 0
                if (!handshakeChecked) {
                    val prefixText = bytes.decodeToString(endIndex = minOf(bytes.size, 3))
                    check(prefixText == "{}\u001E") { "Expected handshake response '{}\\u001E', but got: $prefixText" }
                    handshakeChecked = true
                    println("We have header!")
                    offset = 3
                }

                val frames = decodeFrames(bytes, offset)
                for (frame in frames) {
                    val text = frame.decodeToString(throwOnInvalidSequence = false)
                    println("Frame: $text")
                    text.extractBalance()?.let { return it }
                }
            }
            delay(200L.milliseconds)
        }
        raise(Unavailable)
    }

    private fun decodeFrames(
        bytes: ByteArray,
        startOffset: Int,
    ): List<ByteArray> {
        val frames = mutableListOf<ByteArray>()
        var offset = startOffset
        while (offset < bytes.size) {
            var length = 0
            var shift = 0
            var bytesConsumed = 0
            var varintComplete = false
            while (offset + bytesConsumed < bytes.size) {
                val b = bytes[offset + bytesConsumed++].toInt()
                length = length or ((b and 0x7F) shl shift)
                if ((b and 0x80) == 0) {
                    varintComplete = true
                    break
                }
                shift += 7
            }
            if (!varintComplete) break

            offset += bytesConsumed
            if (offset + length <= bytes.size) {
                val frame = bytes.copyOfRange(offset, offset + length)
                frames.add(frame)
                offset += length
            } else {
                break
            }
        }
        return frames
    }

    // Helpers

    context(_: Raise<DomainError>)
    private fun getConnectionToken(negotiateJson: String) =
        runCatching {
            Json
                .parseToJsonElement(negotiateJson)
                .jsonObject["connectionToken"]
                ?.jsonPrimitive
                ?.content
        }.getOrNull() ?: raise(TotallyBroken("Failed to get connectionToken"))

    // ── MsgPack helpers ──────────────────────────────────────

    private fun msgpackStr(s: String): ByteArray {
        val bytes = s.encodeToByteArray()
        return when {
            bytes.size <= 31 -> {
                byteArrayOf((0xa0 or bytes.size).toByte()) + bytes
            }

            bytes.size <= 0xff -> {
                byteArrayOf(0xd9.toByte(), bytes.size.toByte()) + bytes
            }

            else -> {
                byteArrayOf(
                    0xda.toByte(),
                    (bytes.size shr 8).toByte(),
                    bytes.size.toByte(),
                ) + bytes
            }
        }
    }

    private fun msgpackFixArray(vararg items: ByteArray): ByteArray {
        require(items.size <= 15)
        val header = byteArrayOf((0x90 or items.size).toByte())
        return items.fold(header) { acc, b -> acc + b }
    }

    private fun blazorInvocation(
        invocationId: String,
        target: String,
        args: List<String>,
    ): ByteArray =
        msgpackFixArray(
            byteArrayOf(0x01), // type = invocation
            byteArrayOf(0x80.toByte()), // empty headers map
            msgpackStr(invocationId),
            msgpackStr(target),
            msgpackFixArray(*args.map { msgpackStr(it) }.toTypedArray()),
        )

    private fun varint(value: Int): ByteArray {
        var v = value
        val out = mutableListOf<Byte>()
        while (v >= 0x80) {
            out += ((v and 0x7f) or 0x80).toByte()
            v = v ushr 7
        }
        out += v.toByte()
        return out.toByteArray()
    }

    private fun frame(payload: ByteArray) = varint(payload.size) + payload
}
