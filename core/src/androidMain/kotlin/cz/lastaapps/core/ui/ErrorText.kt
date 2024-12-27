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

package cz.lastaapps.core.ui

import androidx.annotation.StringRes
import cz.lastaapps.core.R
import cz.lastaapps.core.domain.AppText
import cz.lastaapps.core.domain.AppText.Formatted
import cz.lastaapps.core.domain.AppText.Resource
import cz.lastaapps.core.domain.AppText.Rich
import cz.lastaapps.core.domain.error.ApiError
import cz.lastaapps.core.domain.error.ApiError.RatingError
import cz.lastaapps.core.domain.error.ApiError.RatingError.OldAppVersion
import cz.lastaapps.core.domain.error.ApiError.RatingError.OtherProblem
import cz.lastaapps.core.domain.error.ApiError.RatingError.TooManyRequests
import cz.lastaapps.core.domain.error.ApiError.RatingError.Unauthorized
import cz.lastaapps.core.domain.error.ApiError.SyncError
import cz.lastaapps.core.domain.error.ApiError.SyncError.Closed
import cz.lastaapps.core.domain.error.ApiError.SyncError.Problem
import cz.lastaapps.core.domain.error.ApiError.SyncError.Unavailable
import cz.lastaapps.core.domain.error.ApiError.WalletError
import cz.lastaapps.core.domain.error.ApiError.WalletError.InvalidCredentials
import cz.lastaapps.core.domain.error.ApiError.WalletError.TotallyBroken
import cz.lastaapps.core.domain.error.ApiError.WeekNotAvailable
import cz.lastaapps.core.domain.error.CommonError
import cz.lastaapps.core.domain.error.CommonError.AppNotFound
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.AddContact
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.Email
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.Facebook
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.Link
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.Map
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.PhoneCall
import cz.lastaapps.core.domain.error.CommonError.AppNotFound.Telegram
import cz.lastaapps.core.domain.error.CommonError.NotLoggedIn
import cz.lastaapps.core.domain.error.CommonError.WorkTimeout
import cz.lastaapps.core.domain.error.DomainError
import cz.lastaapps.core.domain.error.DomainError.Unknown
import cz.lastaapps.core.domain.error.NetworkError
import cz.lastaapps.core.domain.error.NetworkError.ConnectionClosed
import cz.lastaapps.core.domain.error.NetworkError.NoInternet
import cz.lastaapps.core.domain.error.NetworkError.SerializationError
import cz.lastaapps.core.domain.error.NetworkError.Timeout
import cz.lastaapps.core.domain.error.ParsingError
import cz.lastaapps.core.domain.error.ParsingError.Buffet.DateRangeCannotBeParsed
import cz.lastaapps.core.domain.error.ParsingError.Buffet.DayCannotBeParsed
import cz.lastaapps.core.domain.error.ParsingError.Buffet.DishCannotBeParsed
import cz.lastaapps.core.domain.error.ParsingError.Buffet.MenuCannotBeParsed

@Suppress("NOTHING_TO_INLINE", "FunctionName")
private inline fun T(text: String) = Rich(text)

@Suppress("NOTHING_TO_INLINE", "FunctionName")
private inline fun E(
    @StringRes resId: Int,
) = Resource(resId)

@Suppress("NOTHING_TO_INLINE", "FunctionName")
private inline fun F(
    @StringRes resId: Int,
    vararg args: Any,
) = Formatted(resId, args)

val DomainError.text: AppText
    get() =
        when (this) {
            is NetworkError -> text
            is ParsingError -> text
            is ApiError -> text
            is CommonError -> text
            is Unknown ->
                F(
                    R.string.error_unknown,
                    throwable.localizedMessage ?: "null",
                )
        }

val NetworkError.text: AppText
    get() =
        when (this) {
            ConnectionClosed -> E(R.string.error_network_connection_closed)
            NoInternet -> E(R.string.error_network_no_internet)
            Timeout -> E(R.string.error_network_timeout)
            is SerializationError -> E(R.string.error_network_serialization)
        }

val ParsingError.text: AppText
    get() =
        when (this) {
            DateRangeCannotBeParsed -> E(R.string.error_parsing_date_range)
            DayCannotBeParsed -> E(R.string.error_parsing_day)
            DishCannotBeParsed -> E(R.string.error_parsing_dish)
            MenuCannotBeParsed -> E(R.string.error_parsing_menu)
        }

val ApiError.text: AppText
    get() =
        when (this) {
            WeekNotAvailable -> E(R.string.error_api_week_not_available)
            is SyncError ->
                when (this) {
                    is Problem -> E(R.string.error_api_incomplete_data)
                    Unavailable -> E(R.string.error_api_module_unavailable)
                    Closed -> E(R.string.error_api_menza_cloned)
                }

            is WalletError ->
                when (this) {
                    is TotallyBroken -> E(R.string.error_wallet_login_failed_critical)
                    InvalidCredentials -> E(R.string.error_wallet_login_failed_credentials)
                }

            is RatingError ->
                when (this) {
                    is OldAppVersion -> F(R.string.error_rating_old_app_version, this.reason)
                    is OtherProblem -> F(R.string.error_rating_other_problem, this.code)
                    is TooManyRequests -> F(R.string.error_rating_too_many_requests, this.reason)
                    Unauthorized -> E(R.string.error_rating_unauthorized)
                }
        }

val CommonError.text: AppText
    get() =
        when (this) {
            is WorkTimeout -> E(R.string.error_network_timeout)
            is NotLoggedIn -> E(R.string.error_not_logged_in)
            is AppNotFound ->
                when (this) {
                    AddContact -> E(R.string.error_no_app_contacts)
                    Email -> E(R.string.error_no_app_email)
                    Facebook -> E(R.string.error_no_app_facebook)
                    Link -> E(R.string.error_no_app_browser)
                    Map -> E(R.string.error_no_app_location)
                    PhoneCall -> E(R.string.error_no_app_dial)
                    Telegram -> E(R.string.error_no_app_telegram)
                }
        }
