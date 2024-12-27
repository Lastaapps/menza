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

package cz.lastaapps.api.rating.api

import arrow.core.flatten
import arrow.core.left
import arrow.core.right
import cz.lastaapps.api.core.domain.model.DataLanguage
import cz.lastaapps.api.core.domain.model.DataLanguage.Czech
import cz.lastaapps.api.core.domain.model.DataLanguage.English
import cz.lastaapps.api.core.domain.model.MenzaType
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Strahov
import cz.lastaapps.api.core.domain.model.MenzaType.Agata.Subsystem
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FEL
import cz.lastaapps.api.core.domain.model.MenzaType.Buffet.FS
import cz.lastaapps.api.core.domain.model.MenzaType.Testing.Kocourkov
import cz.lastaapps.api.core.domain.model.dish.DishID
import cz.lastaapps.api.core.domain.model.rating.RatingCategories
import cz.lastaapps.api.core.domain.model.rating.RatingCategory
import cz.lastaapps.api.rating.api.model.RatingClient
import cz.lastaapps.api.rating.data.model.ErrorResponse
import cz.lastaapps.api.rating.data.model.RatingStateResponse
import cz.lastaapps.api.rating.data.model.UserRatingPayload
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.ApiError.RatingError
import cz.lastaapps.core.util.extensions.catchingNetwork
import cz.lastaapps.core.util.extensions.localLogger
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess

internal class RatingAPIImpl(
    ratingClient: RatingClient,
) : RatingAPI {
    private val client = ratingClient.client
    private val log = localLogger()

    private fun MenzaType.toID() =
        when (this) {
            Strahov -> "CTU_STRAHOV"
            is Subsystem -> "CTU_$subsystemId"
            FEL -> "BUFFET_FEL"
            FS -> "BUFFET_FS"
            Kocourkov -> "KOCOURKOV"
        }

    private suspend fun HttpResponse.handleStatus(): Outcome<List<RatingStateResponse>> =
        run {
            if (!status.isSuccess()) {
                val body = body<ErrorResponse>()
                log.e { "Invalid response (${status.description}):\n$body" }
                when (status) {
                    HttpStatusCode.TooManyRequests -> RatingError.TooManyRequests(body.message)
                    HttpStatusCode.BadRequest -> RatingError.OldAppVersion(body.message)
                    HttpStatusCode.Unauthorized -> RatingError.Unauthorized
                    else -> RatingError.OtherProblem(status.value)
                }.left()
            } else {
                body<List<RatingStateResponse>>().right()
            }
        }

    override suspend fun rate(
        menza: MenzaType,
        dishID: DishID,
        name: String,
        language: DataLanguage,
        rating: RatingCategories,
    ): Outcome<List<RatingStateResponse>> =
        catchingNetwork {
            val payload =
                UserRatingPayload(
                    dishID = dishID.value,
                    nameCs = name.takeIf { language == Czech },
                    nameEn = name.takeIf { language == English },
                    taste = rating[RatingCategory.TASTE].toUInt(),
                    portion = rating[RatingCategory.PORTION_SIZE].toUInt(),
                    worthiness = rating[RatingCategory.WORTHINESS].toUInt(),
                )
            client
                .post("v1/rate/${menza.toID()}") {
                    setBody(payload)
                    contentType(ContentType.Application.Json)
                }.handleStatus()
        }.flatten()

    override suspend fun getRatings(menza: MenzaType): Outcome<List<RatingStateResponse>> =
        catchingNetwork {
            client.get("v1/status/${menza.toID()}").handleStatus()
        }.flatten()
}
