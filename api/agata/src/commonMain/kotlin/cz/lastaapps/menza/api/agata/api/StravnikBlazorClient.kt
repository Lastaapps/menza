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

import arrow.core.raise.Raise
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class StravnikBlazorClient(
    private val client: HttpClient,
) {
    private val domain = "https://stravnik.suz.cvut.cz"
    private val negotiateUrl = "$domain/_blazor/negotiate?negotiateVersion=1"
    private val transactionsUrl = "$domain/Transactions/NoSSO"

    suspend fun fetchViaSignalR(descriptor: String): Outcome<Float> =
        outcome {
            // 4. Negotiate — get a LongPolling connection token
            val negotiateJson = catchingNetwork { client.post(negotiateUrl).bodyAsText() }.bind()
            val connectionToken = getConnectionToken(negotiateJson)

            val blazorUrl = "$domain/_blazor?id=$connectionToken"

            // 5. Handshake: plain JSON terminated by ASCII record-separator 0x1e
            catchingNetwork {
                client.post(blazorUrl) {
                    headers { append("Content-Type", "text/plain;charset=UTF-8") }
                    setBody("{\"protocol\":\"blazorpack\",\"version\":1}\u001e")
                }
            }.bind()

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

            // 7. Long-poll loop — balance appears in an early render-batch frame as embedded HTML text
            repeat(8) {
                val bytes = catchingNetwork { client.get(blazorUrl).bodyAsBytes() }.bind()
                if (bytes.isNotEmpty()) {
                    // The binary render-batch frames contain readable UTF-8 HTML snippets
                    val text = bytes.decodeToString(throwOnInvalidSequence = false)
                    text.extractBalance()?.let { return@outcome it }
                }
            }
            raise(Unavailable)
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
