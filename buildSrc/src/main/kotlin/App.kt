
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object App {

    val buildDate: String = ZonedDateTime.now()
        .withZoneSameInstant(ZoneId.of("UTC"))
        .toLocalDate()
        .format(DateTimeFormatter.ISO_DATE)

    const val GROUP = "cz.lastaapps"
    const val APP_ID = "$GROUP.appname" //TODO add app id
    const val VERSION_CODE = 1000000 // 1x major . 2x minor . 2x path . 2x build diff
    const val VERSION_NAME = "1.0.0"
    const val IS_ALPHA = false
    const val IS_BETA = false

    const val USE_PREVIEW = false
    const val MIN_SDK = 21

    // latest official version
    const val COMPILE_SDK = 33
    const val TARGET_SDK = 33

    // preview version, last released android version
    const val PREVIEW_COMPILE_SDK = 33
    const val PREVIEW_TARGET_SDK = 33
//    const val COMPILE_SDK = "android-S"
//    const val TARGET_SDK = "S"
}