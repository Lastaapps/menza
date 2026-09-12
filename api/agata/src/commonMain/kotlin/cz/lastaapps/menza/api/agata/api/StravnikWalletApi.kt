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

            // disable logging, so user credentials/secrets are not accidentally logged
            install(Logging) {
                level = LogLevel.NONE
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
                log.i { "Starting request for ***${username.takeLast(3)}" }
                if (lastUserName != null && lastUserName != username) {
                    logout().bind()
                    lastUserName = null
                }

                // first request will fail, let's login first
                if (lastUserName == username) {
                    // short circuit so we don't call login every time
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
                    log.i { "Success, data fetched" }
                    return@outcome it
                }

                // 4. Slow path: drive the Blazor circuit
                val descriptor =
                    pageHtml.extractDescriptor()
                        ?: raise(Unavailable)

                log.i { "Attempting Blazor fetch" }
                blazorClient.fetchViaSignalR(descriptor).bind()
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

private val BALANCE_REGEX_HTML = Regex("""\[P&#x159;edem\s*([\d\s,.\xA0]+)""")
private val BALANCE_REGEX_UTF8 = Regex("""\[Před?em\s*([\d\s,.]+)""")
private val CSRF_REGEX = Regex("""name="__RequestVerificationToken"\s+[^>]*value="([^"]+)"""")
private val DESCRIPTOR_REGEX =
    Regex(
        """<!--Blazor:(\{[^}]*"descriptor"[^}]*(?:\}[^}]*)*\})-->""",
        RegexOption.DOT_MATCHES_ALL,
    )

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

internal fun String.extractDescriptor(): String? {
    val match = DESCRIPTOR_REGEX.find(this) ?: return null
    return runCatching {
        Json
            .parseToJsonElement(match.groupValues[1])
            .jsonObject["descriptor"]
            ?.jsonPrimitive
            ?.content
    }.getOrNull()
}
