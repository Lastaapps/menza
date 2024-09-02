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

@file:Suppress("ktlint:standard:no-empty-file")

package cz.lastaapps.common

/*
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

object PlayStoreReview {

    private val TAG get() = PlayStoreReview::class.simpleName

    fun askForAppReview(activity: Activity) {
        showOrLegacy(activity) {
            with(AlertDialog.Builder(activity)) {
                setTitle(R.string.review_dialog_title)
                setMessage(R.string.review_dialog_message)
                setPositiveButton(R.string.review_dialog_positive) { dialog, _ ->
                    dialog.dismiss()
                    openPlayStore(activity)
                }
                setNegativeButton(R.string.review_dialog_negative) { dialog, _ ->
                    dialog.dismiss()
                }
                create()
            }.show()
        }
    }

    fun doInAppReview(activity: Activity) {
        showOrLegacy(activity) { openPlayStore(activity) }
    }

    private fun showOrLegacy(activity: Activity, onLegacy: () -> Unit) {
        val manager = ReviewManagerFactory.create(activity)

        //version check
        @SuppressLint("ObsoleteSdkInt")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            onLegacy()
            return
        }

        openInAppReview(
            manager, activity, onLegacy
        )
    }

    private fun openPlayStore(context: Context) {
        Log.i(TAG, "Launching old Play Store review link")

        val url = "https://play.google.com/store/apps/details?id=${context.packageName}"
        val uri = Uri.parse(url)
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun openInAppReview(mgr: ReviewManager, activity: Activity, onFail: () -> Unit) {

        //Google play in app review
        val request = mgr.requestReviewFlow()
        request.addOnCompleteListener { result ->
            if (result.isSuccessful) {

                Log.i(TAG, "A review dialog shown")

                val reviewInfo = result.result
                val flow = mgr.launchReviewFlow(activity, reviewInfo)

                flow.addOnCompleteListener {

                    Log.i(TAG, "A review dialog closed")

                    if (it.isSuccessful) {
                        Toast.makeText(
                            activity,
                            R.string.review_thanks,
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            } else {
                onFail()
            }
        }
    }
}*/
