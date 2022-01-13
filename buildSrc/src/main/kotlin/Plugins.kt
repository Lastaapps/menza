object Plugins {

    const val APPLICATION = "com.android.application"
    const val LIBRARY = "com.android.library"
    const val KOTLIN = "kotlin-android"

    object Java {
        const val LIBRARY = "java-library"
        const val KOTLIN = "kotlin"
    }

    const val KSP = "com.google.devtools.ksp"
    const val KAPT = "kotlin-kapt"
    const val SQLDELIGHT = "com.squareup.sqldelight"
    const val DAGGER_HILT_CLASSPATH =
        "com.google.dagger:hilt-android-gradle-plugin:${Versions.DAGGER_HILT}"
    const val DAGGER_HILT = "dagger.hilt.android.plugin"

    const val PARCELIZE = "kotlin-parcelize"

    const val OSS_LICENSE = "com.google.android.gms.oss-licenses-plugin"

    const val PLAY_SERVICES = "com.google.gms.google-services"
    const val FIREBASE_CRASHLYTICS = "com.google.firebase.crashlytics"

}