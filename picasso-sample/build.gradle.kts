plugins {
  id("com.android.application")
}

android {
  compileSdkVersion(androidCompileSdkVersion)

  defaultConfig {
    minSdkVersion(androidMinSdkVersion)
    targetSdkVersion(androidTargetSdkVersion)

    applicationId = "com.example.picasso"

    setVersionCode(1)
    setVersionName("1.0")
  }

  compileOptions {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  lintOptions {
    textOutput("stdout")
    textReport = true
    lintConfig = file("lint.xml")

    // https://github.com/square/okhttp/issues/896
    ignore("InvalidPackage")
  }
}

repositories {
  google()
  mavenCentral()
}

dependencies {
  compileOnly(deps.annotations)
  implementation(deps.appcompat)
  implementation(deps.fragment)
  implementation(project(":picasso"))
}