rootProject.name = "picasso-root"

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
  }
  plugins {
    id("com.android.application") version "7.0.0-alpha06"
    id("com.android.library") version "7.0.0-alpha06"
  }
}

include("picasso")
include("picasso-pollexor")
include("picasso-sample")