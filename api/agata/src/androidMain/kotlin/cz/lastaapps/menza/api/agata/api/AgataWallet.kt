/*
 *    Copyright 2023, Petr Laštovička as Lasta apps, All rights reserved
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
import cz.lastaapps.core.util.extensions.catchingNetwork
import io.ktor.client.HttpClient
import io.ktor.client.plugins.BrowserUserAgent
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.Parameters
import it.skrape.core.htmlDocument
import java.net.URLDecoder

actual class AgataWallet actual constructor(httpClient: HttpClient) {

    private val client: HttpClient

    init {
        client = httpClient.config {
            // Disable redirects, because when I was testing it with Python I got into loop sometimes
            // Also it has to extract some cookies from some of the requests, so redirects manually handled
            followRedirects = false

            BrowserUserAgent()
        }
    }

    /// Get balance from Agata (wrapper with Outcome)
    actual suspend fun getBalance(username: String, password: String): Outcome<Float> = catchingNetwork {
        getBalanceRaw(username, password)
    }

    /// Get balance from Agata
    private suspend fun getBalanceRaw(username: String, password: String): Float {
        // Go to the auth provider
        var response = client.get("https://agata.suz.cvut.cz/secure/index.php")
        var url = response.headers["Location"]
        response = client.get(url!!)

        // Get new url params
        val body = response.bodyAsText()
        val returnUrl = Regex("var returnURL = \"(.+?)\"").find(body)?.groups?.get(1)?.value
        val otherParams = Regex("var otherParams = \"(.+?)\"").find(body)?.groups?.get(1)?.value
        url = "${returnUrl}&entityID=https://idp2.civ.cvut.cz/idp/shibboleth${otherParams}"

        // Get to SSO
        response = client.get(url)
        response = client.get(response.headers["Location"]!!)

        // Extract JSESSIONID cookie
        val jsessionid = response.headers["Set-Cookie"]!!.split(";")[0].split("=")[1]

        // Resolve SSO
        url = "https://idp2.civ.cvut.cz${response.headers["Location"]}"
        client.get(url)
        response = client.post(url) {
            setBody(FormDataContent(Parameters.build {
                append("j_username", username)
                append("j_password", password)
                append("_eventId_proceed", "")
            }))
            Cookie("JSESSIONID", jsessionid)
            header("Referer", url)
            header("Content-Type", "application/x-www-form-urlencoded")
        }

        // Extract response codes from html
        var html = htmlDocument(response.bodyAsText())
        var relayState: String? = null
        var samlResponse: String? = null
        html.findAll("input").forEach {
            if (it.attribute("name") == "RelayState") {
                relayState = URLDecoder.decode(it.attribute("value"), "UTF-8")
            }
            if (it.attribute("name") == "SAMLResponse") {
                samlResponse = it.attribute("value")
            }
        }

        // Send the shit back to Agata and get session cookie
        response = client.post("https://agata.suz.cvut.cz/Shibboleth.sso/SAML2/POST") {
            setBody(FormDataContent(Parameters.build {
                append("RelayState", relayState!!)
                append("SAMLResponse", samlResponse!!)
            }))
            header("Referer", "https://idp2.civ.cvut.cz/")
        }
        val sessionCookie = response.headers["Set-Cookie"]?.split(";")?.get(0)

        // Get balance from Agata
        response = client.get("https://agata.suz.cvut.cz/secure/index.php") {
            // The session cookie has variable name, so using raw headers here
            header("Cookie", sessionCookie)
        }
        html = htmlDocument(response.bodyAsText())

        // Parse
        return html.findFirst("h4 span.badge").text.lowercase()
            .replace("kč", "").replace(",", ".").replace(" ", "").trim().toFloat()
    }
}

