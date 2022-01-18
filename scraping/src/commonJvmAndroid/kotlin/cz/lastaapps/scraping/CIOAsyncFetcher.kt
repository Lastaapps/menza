/*
 *    Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
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

package cz.lastaapps.scraping

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import it.skrape.fetcher.NonBlockingFetcher
import it.skrape.fetcher.Request
import it.skrape.fetcher.Result

/**
 * Stolen from AsyncFetcher, witch failed with a SocketException
 * without any obvious reason, probably because of the Apache ktor engine used.
 * With CIO everything is working fine so far
 */
object CIOAsyncFetcher : NonBlockingFetcher<Request> {

    override val requestBuilder: Request get() = Request()

    override suspend fun fetch(request: Request): Result =
        configuredClient(request).toResult()

    private suspend fun configuredClient(request: Request): HttpResponse {
        return HttpClient(CIO) {
            followRedirects = request.followRedirects
            engine {
                requestTimeout = request.timeout.toLong()
                endpoint {
                    connectTimeout = request.timeout.toLong()
                    keepAliveTime = request.timeout.toLong()
                    connectAttempts = 1
                }
            }
        }.request<HttpResponse> {
            url(request.url)
        }
    }

    //Stolen from skrape.it
    private suspend fun HttpResponse.toResult(): Result = Result(
        responseBody = this.readText(),
        responseStatus = this.toStatus(),
        contentType = this.contentType()?.toString()?.replace(" ", ""),
        headers = this.headers.flattenEntries()
            .associateBy({ item -> item.first }, { item -> this.headers[item.first]!! }),
        cookies = emptyList(), //this.setCookie().map { cookie -> cookie.toDomainCookie(this.request.url.toString().urlOrigin) },
        baseUri = this.request.url.toString()
    )

    //Stolen from skrape.it
    private fun HttpResponse.toStatus() = Result.Status(this.status.value, this.status.description)
}
