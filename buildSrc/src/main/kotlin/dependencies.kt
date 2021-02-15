@file:Suppress("ClassName")

import org.gradle.api.JavaVersion

val javaVersion = JavaVersion.VERSION_1_8

const val libraryGroupId = "io.github.minyushov"
const val libraryVersion = "3.0.0-6"

const val androidMinSdkVersion = 14
const val androidCompileSdkVersion = 30
const val androidTargetSdkVersion = 30
const val androidBuildToolsVersion = "30.0.3"

object deps {
  const val okhttp = "com.squareup.okhttp3:okhttp:4.9.1"
  const val mockWebServer = "com.squareup.okhttp3:mockwebserver:4.9.1"

  const val pollexor = "com.squareup:pollexor:2.0.4"
  const val annotations = "androidx.annotation:annotation:1.1.0"
  const val appcompat = "androidx.appcompat:appcompat:1.2.0"
  const val fragment = "androidx.fragment:fragment:1.3.0"
  const val exif = "androidx.exifinterface:exifinterface:1.3.2"

  const val junit = "junit:junit:4.13.2"
  const val truth = "com.google.truth:truth:0.36"
  const val robolectric = "org.robolectric:robolectric:4.5.1"
  const val mockito = "org.mockito:mockito-core:3.7.7"
}