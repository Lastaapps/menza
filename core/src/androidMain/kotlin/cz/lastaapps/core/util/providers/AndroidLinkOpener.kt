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

package cz.lastaapps.core.util.providers

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import arrow.core.Either
import cz.lastaapps.core.domain.Outcome
import cz.lastaapps.core.domain.error.CommonError

internal class AndroidLinkOpener(
    private val context: Context,
) : LinkOpener {

    private fun <T> runCatchingAppNotFound(block: () -> T) =
        Either.catchOrThrow<ActivityNotFoundException, T>(block)

    override fun openLink(url: String): Outcome<Unit> = runCatchingAppNotFound {
        Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .let {
                context.startActivity(it)
            }
    }.mapLeft { CommonError.AppNotFound.Link }

    override fun writeEmail(email: String): Outcome<Unit> = runCatchingAppNotFound {
        error("Not yet implemented")
    }.mapLeft { CommonError.AppNotFound.Email }

    override fun callPhoneNumber(number: String): Outcome<Unit> = runCatchingAppNotFound {
        error("Not yet implemented")
    }.mapLeft { CommonError.AppNotFound.PhoneCall }

    override fun openAddress(address: String): Outcome<Unit> = runCatchingAppNotFound {
        error("Not yet implemented")
    }.mapLeft { CommonError.AppNotFound.Map }

    override fun openGeo(lat: Float, long: Float): Outcome<Unit> = runCatchingAppNotFound {
        error("Not yet implemented")
    }.mapLeft { CommonError.AppNotFound.Map }

    override fun openTelegram(groupUrl: String): Outcome<Unit> = openLink(groupUrl)
        .mapLeft { CommonError.AppNotFound.Telegram }

    override fun openFacebookPage(pageUrl: String): Outcome<Unit> = runCatchingAppNotFound {
        var uri = Uri.parse(pageUrl)
        try {
            val applicationInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getApplicationInfo(
                        "com.facebook.katana",
                        PackageManager.ApplicationInfoFlags.of(0),
                    )
                } else {
                    context.packageManager.getApplicationInfo(
                        "com.facebook.katana", 0
                    )
                }

            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$pageUrl")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }.mapLeft { CommonError.AppNotFound.Facebook }
}
