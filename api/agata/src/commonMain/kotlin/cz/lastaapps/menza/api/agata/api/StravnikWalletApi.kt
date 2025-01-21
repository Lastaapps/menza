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

package cz.lastaapps.menza.api.agata.api

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

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
                if (lastUserName != null) {
                    // short circuit so we don't call login every time
                    val firstAttempt =
                        getData()
                            .map { it.bodyAsText().processBody() }
                            .flatten()

                    when (firstAttempt) {
                        is Right -> {
                            log.i { "Success, data fetched (quick)" }
                            return@outcome firstAttempt.value
                        }

                        is Left -> {} // login may be needed
                    }
                }

                login(username, password).bind().let { response ->
                    if (response.status != HttpStatusCode.Found) {
                        raise(ApiError.WalletError.InvalidCredentials)
                    }
                }

                // I have a suspicion that there is a race condition in the backend
                // if the second request is to quick
                // So I add delay and try it multiple times
                // ----------------------------------------------------------------
                // Now I checked and actually it is a case somehow
                // After login server may return the correct page,
                // but without the account balance.
                // It is usually fine for the second request,
                // but I have seen cases when it skip trough 5 cycles with not troubles.
                // ----------------------------------------------------------------
                // So I also reproduced the behaviour in browser.
                // If the server is overloaded or something (most often when I reload the page quickly),
                // the web page may contain no balance data.
                // Therefore, the retries should be more spaced out than tens of milliseconds.
                // If the balance data are missing, the "Vklad na konto" button is also missing.
                val iterations = 12
                repeat(iterations) { i ->
                    getData()
                        .bind()
                        .bodyAsText()
                        .processBody()
                        .onRight {
                            log.i { "Success, data fetched" }
                            lastUserName = username
                            return@outcome it
                        }.onLeft {
                            if (i == iterations - 1) {
                                log.e { "Failed to fetch data: $it" }
                                // raise(it)
                                return@repeat
                            }
                        }
                    delay(690.milliseconds)
                }
                getData()
                    .bind()
                    .bodyAsText()
                    .takeIf { it.contains("Os.") && !it.contains("Vklad na konto") }
                    ?.let {
                        raise(ApiError.WalletError.Unavailable)
                    }
                raise(ApiError.WalletError.TotallyBroken("Failed after all the iterations"))
            }
        }

    private suspend fun login(
        username: String,
        password: String,
    ) = catchingNetwork {
        log.i { "Logging in" }
        client.submitForm(
            "$domain/Identity/Account/Login",
            parameters {
                append("Input.UserName", username)
                append("Input.Password", password)
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

    private fun String.processBody(): Outcome<Float> =
        """\[P&#x159;edem ((?:&#xA0;|\d|\s)+(?:[,.]\d+)?)"""
            .toRegex()
            .let { regex ->
                Either
                    .runCatching {
                        regex
                            .find(this@processBody, startIndex = 5800)
                            ?.groups
                            ?.get(1)
                            ?.value
                    }.fold(
                        { it },
                        {
                            log.e(it) { "Finding regex failed" }
                            log.e { "The error body was:\n$this" }
                            return ApiError.WalletError
                                .TotallyBroken(
                                    "Rexex did not match:\n" +
                                        this@processBody,
                                ).left()
                        },
                    )
            }?.replace(',', '.')
            ?.replace(" ", "")
            ?.replace("&#xA0;", "")
            ?.toFloatOrNull()
            ?.right()
            ?: ApiError.WalletError
                .TotallyBroken(
                    "Failed to parse number:\n" +
                        this@processBody,
                ).left()
}
