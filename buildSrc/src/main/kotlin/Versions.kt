
import org.gradle.api.JavaVersion

object Versions {

    val JAVA = JavaVersion.VERSION_11
    const val JVM_TARGET = "11"

    const val GRADLE = "7.2.1"

    //Versions
    //Studio
    const val DESUGAR = "1.1.5"

    //JetBrains
    const val KOTLIN = "1.6.21"
    const val KOTLIN_LANGUAGE_VERSION = "1.6"
    const val KSP = "$KOTLIN-1.0.5"
    const val COROUTINES = "1.6.2"
    const val KTOR = "2.0.2"

    //androidx
    const val ACTIVITY = "1.4.0"
    const val ANNOTATION = "1.4.0-rc01"
    const val APPCOMPAT = "1.4.1"
    const val COLLECTION = "1.2.0"
    const val CONSTRAINTLAYOUT = "2.1.3"
    const val CORE = "1.8.0"
    const val DAGGER_HILT = "2.38.1"
    const val DATASTORE = "1.0.0"
    const val DOCUMENT_FILE = "1.1.0-alpha01"
    const val EMOJI = "1.1.0"
    const val FRAGMENT = "1.4.0"
    const val HILT_COMMON = "1.0.0"
    const val HILT_COMPILER = "1.0.0"
    const val HILT_NAVIGATION = "1.0.0"
    const val HILT_VIEWMODEL = "1.0.0-alpha03"
    const val HILT_WORK = "1.0.0"
    const val LIFECYCLE = "2.4.1"
    const val NAVIGATION = "2.4.1"
    const val PREFERENCES = "1.2.0-rc01"
    const val RECYCLER_VIEW = "1.3.0-alpha01"
    const val ROOM = "2.4.2"
    const val SPLASHSCREEN = "1.0.0-beta02"
    const val STARTUP = "1.1.1"
    const val SWIPE_REFRESH_LAYOUT = "1.2.0-alpha01"
    const val TRACING = "1.1.0"
    const val VECTOR_DRAWABLES = "1.2.0-alpha02"
    const val WINDOW_MANAGER = "1.0.0"
    const val WORK = "2.7.1"


    //compose
    const val COMPOSE = "1.2.0-beta03"
    const val COMPOSE_COMPILER = COMPOSE
    const val COMPOSE_STABLE = "1.1.1"
    const val COMPOSE_COMPILER_STABLE = COMPOSE_STABLE
    const val COMPOSE_MATERIAL_3 = "1.0.0-alpha13"
    const val CONSTRAINTLAYOUT_COMPOSE = "1.0.0"
    const val VIEWMODEL_COMPOSE = LIFECYCLE
    const val HILT_NAVIGATION_COMPOSE = "1.0.0"

    //google
    const val GOOGLE_MATERIAL = "1.6.1"
    const val PLAY_SERVICES = "1.8.1"
    const val ACCOMPANIST = "0.24.9-beta"

    //firebase
    const val FIREBASE_BOM = "29.3.1"

    //others
    const val KOTEST = "5.3.0"
    const val KOTLINX_DATETIME = "0.3.3"
    const val SQLDELIGHT = "1.5.3"
    const val KM_LOGGING = "1.1.1"
    const val COIL = "2.1.0"
    const val SKRAPE_IT = "1.2.1"
    const val ABOUT_LIBRARIES = "10.3.0"

    //testing android
    const val TEST_JUNIT = "4.13.2"
    const val TEST_ARCH = "2.1.0"
    const val TEST_ANDROIDX = "1.4.0"
    const val TEST_ANDROIDX_JUNIT = "1.1.3"
    const val TEST_KOTLIN_COROUTINES = COROUTINES
    const val TEST_ROBOELECTRIC = "4.6.1"
    const val TEST_GOOGLE_TRUTH = "1.1.3"
    const val TEST_ESPRESSO_CORE = "3.4.0"

}
