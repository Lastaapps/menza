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

package cz.lastaapps.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log

object Communication {
    private val TAG get() = Communication::class.simpleName

    private const val FACEBOOK_URL = "https://www.facebook.com/lastaapps/"
    private const val GITHUB_URL = "https://github.com/lastaapps/"
    private const val MATRIX_URL = "https://matrix.to/#/#lastaapps_menza:matrix.org"
    private const val TELEGRAM_URL = "https://t.me/lasta_apps"
    private const val DISCORD_URL = "https://discord.com/users/694264686388052049"
    private const val PLAY_STORE_URL = "https://play.google.com/store/apps/developer?id=Lasta+apps"

    fun openFacebook(context: Context) = openFacebookPage(context, FACEBOOK_URL)

    fun openFacebookPage(
        context: Context,
        url: String,
    ) {
        var uri = Uri.parse(url)
        try {
            val applicationInfo =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.packageManager.getApplicationInfo(
                        "com.facebook.katana",
                        PackageManager.ApplicationInfoFlags.of(0),
                    )
                } else {
                    context.packageManager.getApplicationInfo(
                        "com.facebook.katana",
                        0,
                    )
                }

            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        Log.i(TAG, "Opening facebook link: $uri")

        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun openGithub(context: Context) = openUrl(context, GITHUB_URL)

    fun openProjectsGithub(
        context: Context,
        name: String,
    ) = openUrl(context, "$GITHUB_URL$name/")

    fun openProjectsCommits(
        context: Context,
        name: String,
    ) = openUrl(context, "$GITHUB_URL$name/commits/")

    fun openMatrix(context: Context) = openUrl(context, MATRIX_URL)

    fun openTelegram(context: Context) = openUrl(context, TELEGRAM_URL)

    fun openDiscord(context: Context) = openUrl(context, DISCORD_URL)

    fun openPlayStore(context: Context) = openUrl(context, PLAY_STORE_URL)

    private fun openUrl(
        context: Context,
        url: String,
    ) {
        Log.i(TAG, "Opening link: $url")

        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}
