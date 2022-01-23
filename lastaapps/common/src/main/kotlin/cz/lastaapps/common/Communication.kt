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

package cz.lastaapps.common

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log

object Communication {

    private val TAG get() = Communication::class.simpleName

    private const val facebookUrl = "https://www.facebook.com/lastaapps/"
    private const val githubUrl = "https://github.com/lastaapps/"
    private const val telegramUrl = "https://t.me/lasta_apps"
    private const val playStoreUrl = "https://play.google.com/store/apps/developer?id=Lasta+apps"

    fun openFacebook(context: Context) = openFacebookPage(context, facebookUrl)

    fun openFacebookPage(context: Context, url: String) {
        var uri = Uri.parse(url)
        try {
            val applicationInfo =
                context.packageManager.getApplicationInfo("com.facebook.katana", 0)

            if (applicationInfo.enabled) {
                uri = Uri.parse("fb://facewebmodal/f?href=$url")
            }
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        Log.i(TAG, "Opening facebook link: $uri")

        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    fun openGithub(context: Context) = openUrl(context, githubUrl)

    fun openProjectsGithub(context: Context, name: String) =
        openUrl(context, "$githubUrl$name/")

    fun openProjectsCommits(context: Context, name: String) =
        openUrl(context, "$githubUrl$name/commits/")

    fun openTelegram(context: Context) = openUrl(context, telegramUrl)

    fun openPlayStore(context: Context) = openUrl(context, playStoreUrl)

    private fun openUrl(context: Context, url: String) {

        Log.i(TAG, "Opening link: $url")

        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

}