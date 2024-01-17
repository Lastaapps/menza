# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile



# Serialization
# Keep `Companion` object fields of serializable classes.
# This avoids serializer lookup through `getDeclaredClasses` as done for named companion objects.
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}

# Keep `serializer()` on companion objects (both default and named) of serializable classes.
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <2>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep `INSTANCE.serializer()` of serializable objects.
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# @Serializable and @Polymorphic are used at runtime for polymorphic serialization.
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

# Decompose
-keep class com.arkivanov.decompose.extensions.compose.mainthread.SwingMainThreadChecker

# Idk, I hope it doesn't crash
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn groovy.**
-dontwarn io.ktor.client.features.HttpTimeout$Feature
-dontwarn io.ktor.client.features.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.features.HttpTimeout
-dontwarn io.ktor.client.features.HttpTimeoutKt
-dontwarn io.ktor.network.sockets.ConnectTimeoutException
-dontwarn io.ktor.network.sockets.SocketTimeoutException
-dontwarn java.awt.**
-dontwarn java.beans.**
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.sql.JDBCType
-dontwarn javax.**
-dontwarn org.apache.bsf.BSFManager
-dontwarn org.codehaus.groovy.**
-dontwarn org.codehaus.janino.ClassBodyEvaluator
-dontwarn org.ietf.jgss.**
-dontwarn sun.**
