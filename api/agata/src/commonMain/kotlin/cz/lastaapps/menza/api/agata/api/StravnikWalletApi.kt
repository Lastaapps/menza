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

import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.WalletError.InvalidCredentials
import cz.lastaapps.core.domain.error.ApiError.WalletError.Unavailable
import cz.lastaapps.core.domain.outcome
import cz.lastaapps.core.util.extensions.catchingNetwork
import cz.lastaapps.core.util.extensions.localLogger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface StravnikWalletApi : WalletApi

internal class StravnikWalletApiImpl(
    httpClient: HttpClient,
) : StravnikWalletApi {
    private val log = localLogger()

    private val client: HttpClient =
        httpClient.config {
            // Disable redirects, because when I was testing it with Python I got into loop sometimes
            // Also it has to extract some cookies from some of the requests, so redirects manually handled
            followRedirects = false
            // Because of many redirects
            expectSuccess = false

            install(HttpTimeout) {
                socketTimeoutMillis = 10_000
                requestTimeoutMillis = 10_000
            }

            // disable logging, so user credentials/secrets are not accidentally logged
            install(Logging) {
                level = LogLevel.HEADERS
            }
            install(HttpCookies)

            BrowserUserAgent()
        }

    private val blazorClient = StravnikBlazorClient(client)

    // needed to not mess up cookies
    private val mutex = Mutex()

    private val domain = "https://stravnik.suz.cvut.cz"
    private var lastUserName: String? = null

    override suspend fun getBalance(
        username: String,
        password: String,
    ): Outcome<Float> =
        mutex.withLock {
            outcome {
                log.i {
                    val lastIndex = username.lastIndex
                    val obfuscated =
                        username
                            .mapIndexed { index, ch -> if (index == 0 || index == lastIndex) ch else '*' }
                            .joinToString(separator = "")
                    "Starting request for $obfuscated"
                }

                logout().bind()

                if (lastUserName != null && lastUserName != username) {
                    logout().bind()
                    lastUserName = null
                }

                // Login cookies may be fresh from previous fetches
                if (lastUserName == username) {
                    val pageHtml = getData().bind().bodyAsText()

                    pageHtml.extractBalance()?.let {
                        log.i { "Success, data fetched (quick)" }
                        return@outcome it
                    }

                    pageHtml.extractDescriptor()?.let { descriptor ->
                        log.i { "Attempting Blazor fetch (quick)" }
                        return@outcome blazorClient.fetchViaSignalR(descriptor).bind()
                    }
                }

                // 1. GET login page — extract CSRF token
                val loginPageHtml =
                    catchingNetwork {
                        client.get("$domain/Identity/Account/Login").bodyAsText()
                    }.bind()
                val csrf = loginPageHtml.extractCsrfToken()

                // 2. POST login
                login(username, password, csrf).bind().let { response ->
                    if (response.status != HttpStatusCode.Found) {
                        raise(InvalidCredentials)
                    }
                }
                lastUserName = username

                // 3. GET Transactions/NoSSO
                val pageHtml = getData().bind().bodyAsText()
                pageHtml.extractBalance()?.let {
                    log.i { "Success, data fetched from initial HTML response" }
                    return@outcome it
                }

                // 4. Slow path: drive the Blazor circuit
                val blazor =
                    pageHtml.extractDescriptor()
                        ?: raise(Unavailable)

                log.i { "Attempting Blazor fetch" }
                blazorClient.fetchViaSignalR(blazor.descriptor, blazor.sequence).bind()
            }
        }

    private suspend fun login(
        username: String,
        password: String,
        csrf: String?,
    ) = catchingNetwork {
        log.i { "Logging in" }
        client.submitForm(
            "$domain/Identity/Account/Login",
            parameters {
                append("Input.UserName", username)
                append("Input.Password", password)
                csrf?.let { append("__RequestVerificationToken", it) }
            },
        )
    }

    private suspend fun getData() =
        catchingNetwork {
            log.i { "Getting data" }
            client.get("$domain/Transactions/NoSSO")
        }

    private suspend fun logout() =
        catchingNetwork {
            log.i { "Logging out" }
            client.post("$domain/Identity/Account/LogOut")
        }
}

private val BALANCE_REGEX_HTML = Regex("""(?:P&#x159;edem|Předem)\s+([-+]?\d[\d\s]*[,.]\d+)""", RegexOption.IGNORE_CASE)
private val BALANCE_REGEX_UTF8 = Regex("""(?:P&#x159;edem|Předem)\s+([-+]?\d[\d\s]*[,.]\d+)""", RegexOption.IGNORE_CASE)
private val CSRF_REGEX = Regex("""name="__RequestVerificationToken"\s+[^>]*value="([^"]+)"""")
private val DESCRIPTOR_REGEX =
    """<!--Blazor:(\{[^>]*"descriptor"[^>]*\})-->""".toRegex()

internal fun String.extractBalance(): Float? {
    val raw =
        BALANCE_REGEX_HTML.find(this)?.groupValues?.getOrNull(1)
            ?: BALANCE_REGEX_UTF8.find(this)?.groupValues?.getOrNull(1)
            ?: return null
    return raw
        .replace(',', '.')
        .replace("\u00a0", "")
        .replace(" ", "")
        .replace("&#xA0;", "")
        .trim()
        .toFloatOrNull()
}

internal fun String.extractCsrfToken(): String? = CSRF_REGEX.find(this)?.groupValues?.getOrNull(1)

internal data class BlazorDescriptor(
    val descriptor: String,
    val sequence: Int,
)

internal fun String.extractDescriptor(): BlazorDescriptor? {
    val match = DESCRIPTOR_REGEX.find(this) ?: return null
    return runCatching {
        val json = Json.parseToJsonElement(match.groupValues[1]).jsonObject
        val descriptor = json["descriptor"]?.jsonPrimitive?.content ?: return null
        val sequence = json["sequence"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0
        BlazorDescriptor(descriptor, sequence)
    }.getOrNull()
}
