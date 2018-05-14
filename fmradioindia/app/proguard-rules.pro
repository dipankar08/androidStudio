# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-dontnote com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-dontwarn org.apache.commons.**
-dontwarn org.apache.http.**
-keep class org.apache.http.** { *; }
-dontwarn jcifs.http.NetworkExplorer
-keep class android.support.v4.** { *; }
-dontnote android.support.v4.**
-keep class android.support.v7.** { *; }
-dontnote android.support.v7.**

-dontwarn sun.misc.Unsafe
-dontwarn okio.Okio
-dontwarn com.bumptech.glide.load.resource.bitmap.VideoDecoder
-dontwarn okio.DeflaterSink