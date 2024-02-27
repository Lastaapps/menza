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

# Keep all the exceptions names
-keepnames class * extends java.lang.Throwable

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

# Don't print notes about potential mistakes or omissions in the configuration for kotlinx-serialization classes
# See also https://github.com/Kotlin/kotlinx.serialization/issues/1900
-dontnote kotlinx.serialization.**

# Serialization core uses `java.lang.ClassValue` for caching inside these specified classes.
# If there is no `java.lang.ClassValue` (for example, in Android), then R8/ProGuard will print a warning.
# However, since in this case they will not be used, we can disable these warnings
-dontwarn kotlinx.serialization.internal.ClassValueReferences


# Decompose
-keep class com.arkivanov.decompose.extensions.compose.mainthread.SwingMainThreadChecker

# Those are only referenced from the Apache engine I'm not using
-dontwarn io.ktor.client.features.HttpTimeout$Feature
-dontwarn io.ktor.client.features.HttpTimeout$HttpTimeoutCapabilityConfiguration
-dontwarn io.ktor.client.features.HttpTimeout
-dontwarn io.ktor.client.features.HttpTimeoutKt
-dontwarn io.ktor.network.sockets.ConnectTimeoutException
-dontwarn io.ktor.network.sockets.SocketTimeoutException

# Idk, I hope it doesn't crash
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
-dontwarn groovy.**
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
