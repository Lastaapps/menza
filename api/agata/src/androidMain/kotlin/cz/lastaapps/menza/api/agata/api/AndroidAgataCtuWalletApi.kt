/*
 *    Copyright 2024, Petr Laštovička as Lasta apps, All rights reserved
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

import arrow.core.flatten
import arrow.core.left
import arrow.core.raise.nullable
import arrow.core.right
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.WalletError
import cz.lastaapps.core.util.extensions.catchingNetwork
import io.ktor.client.HttpClient
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import java.net.URLDecoder

internal class AndroidAgataCtuWalletApi(
    httpClient: HttpClient,
) : AgataCtuWalletApi {
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

            BrowserUserAgent()
        }

    // Get balance from Agata
    // Originally written by Marekkon5
    override suspend fun getBalance(
        username: String,
        password: String,
    ): Outcome<Float> =
        catchingNetwork {
            nullable {
                // Go to the auth provider
                client
                    .get("https://agata.suz.cvut.cz/secure/index.php")
                    .headers[HttpHeaders.Location]
                    .bind()
                    .let { url -> client.get(url) }
                    // Get new url params
                    .let { request ->
                        val body = request.bodyAsText()
                        val returnUrl =
                            Regex("var returnURL = \"(.+?)\"")
                                .find(body)
                                ?.groups
                                ?.get(1)
                                ?.value
                                .bind()
                        val otherParams =
                            Regex("var otherParams = \"(.+?)\"")
                                .find(body)
                                ?.groups
                                ?.get(1)
                                ?.value
                                .bind()
                        "$returnUrl&entityID=https://idp2.civ.cvut.cz/idp/shibboleth$otherParams"
                    }
                    // Get to SSO
                    .let { url -> client.get(url).headers[HttpHeaders.Location].bind() }
                    .let { sso -> client.get(sso) }
                    .let { ssoResponse ->
                        // Extract JSESSIONID cookie
                        val jsessionid =
                            ssoResponse.headers[HttpHeaders.SetCookie]
                                ?.split(";")
                                ?.firstOrNull()
                                ?.split("=")
                                ?.getOrNull(1)
                                .bind()

                        // Resolve SSO
                        val url =
                            "https://idp2.civ.cvut.cz${ssoResponse.headers[HttpHeaders.Location]}"

                        // result ignored, must happen
                        client.get(url)

                        client.post(url) {
                            setBody(
                                FormDataContent(
                                    Parameters.build {
                                        append("j_username", username)
                                        append("j_password", password)
                                        append("_eventId_proceed", "")
                                    },
                                ),
                            )
                            Cookie("JSESSIONID", jsessionid)
                            header(HttpHeaders.Referrer, url)
                            header(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        }
                    }
                    // Extract response codes from html
                    .bodyAsText()
                    .let { html ->
                        var relayState: String? = null
                        var samlResponse: String? = null

                        val inputRegex =
                            """<input[^>]*name="([^"]+)"[^>]*value="([^"]+)"[^>]""".toRegex()
                        inputRegex
                            .findAll(html)
                            .forEach {
                                val name = it.groups[1].bind().value
                                val value = it.groups[2].bind().value
                                when (name) {
                                    "RelayState" -> relayState = URLDecoder.decode(value, "UTF-8")
                                    "SAMLResponse" -> samlResponse = value
                                }
                            }
                        if (relayState == null || samlResponse == null) {
                            return@catchingNetwork WalletError.InvalidCredentials.left()
                        }

                        // Send the shit back to Agata and get session cookie
                        val response =
                            client.post("https://agata.suz.cvut.cz/Shibboleth.sso/SAML2/POST") {
                                setBody(
                                    FormDataContent(
                                        Parameters.build {
                                            append("RelayState", relayState.bind())
                                            append("SAMLResponse", samlResponse.bind())
                                        },
                                    ),
                                )
                                header(HttpHeaders.Referrer, "https://idp2.civ.cvut.cz/")
                            }

                        response.headers[HttpHeaders.SetCookie]
                            ?.split(";")
                            ?.getOrNull(0)
                            .bind()
                    }.let { sessionCookie ->
                        // Get balance from Agata
                        client.get("https://agata.suz.cvut.cz/secure/index.php") {
                            // The session cookie has variable name, so using raw headers here
                            header("Cookie", sessionCookie)
                        }
                    }.let { finalResponse ->
                        """<h4><span[^>]*>(?:<span[^>]*>)?([\d, ]+) Kč<"""
                            .toRegex()
                            .find(finalResponse.bodyAsText())
                            ?.groups
                            ?.get(1)
                            ?.value
                            .bind()
                            .replace(",", ".")
                            .replace(" ", "")
                            .trim()
                            .toFloatOrNull()
                            .bind()
                    }
            }?.right() ?: WalletError.TotallyBroken().left()
        }.flatten()
}
