@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
  id("com.android.library")
  `maven-publish`
  signing
  checkstyle
}

android {
  compileSdkVersion(androidCompileSdkVersion)

  defaultConfig {
    buildToolsVersion = androidBuildToolsVersion
    minSdkVersion(androidMinSdkVersion)
    targetSdkVersion(androidTargetSdkVersion)
    consumerProguardFile("consumer-proguard-rules.txt")
  }

  compileOptions {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
  }

  lintOptions {
    textOutput("stdout")
    textReport = true
    lintConfig = file("lint.xml")
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }
}

repositories {
  google()
  mavenCentral()
}

tasks.register<Checkstyle>("checkstyle") {
  group = "verification"
  configFile = rootProject.file("checkstyle.xml")
  source("src/main/java")
  ignoreFailures = false
  isShowViolations = true
  include("**/*.java")
  classpath = files()
}

val sourcesJar by tasks.registering(Jar::class) {
  archiveClassifier.set("sources")

  val android = project
    .extensions
    .getByType<com.android.build.gradle.LibraryExtension>()

  val sourceSet = android
    .sourceSets
    .findByName("main")
    ?: throw GradleException("Unable to find 'main' source set")

  from(sourceSet.java.srcDirs)
}

afterEvaluate {
  tasks.named("check").configure {
    dependsOn("checkstyle")
  }

  val properties = rootProject
    .file("local.properties")
    .takeIf { it.isFile }
    ?.let { file ->
      Properties()
        .apply { load(file.inputStream()) }
    }

  publishing {
    publications {
      create<MavenPublication>("maven") {
        groupId = libraryGroupId
        artifactId = project.name
        version = libraryVersion

        from(components.findByName("release"))
        artifact(sourcesJar)

        pom {
          name.set(artifactId)
          description.set("A powerful image downloading and caching library for Android")
          url.set("https://github.com/minyushov/picasso")
          developers {
            developer {
              id.set("minyushov")
              name.set("Semyon Minyushov")
              email.set("minyushov@gmail.com")
            }
          }
          licenses {
            license {
              name.set("Apache-2.0")
              url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
          scm {
            connection.set("https://github.com/minyushov/picasso.git")
            developerConnection.set("https://github.com/minyushov/picasso.git")
            url.set("https://github.com/minyushov/picasso/tree/master")
          }
        }
      }
    }
    repositories {
      maven {
        name = "sonatype"
        setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
        credentials {
          username = properties["SONATYPE_USER"]
          password = properties["SONATYPE_PASSWORD"]
        }
      }
    }
  }

  signing {
    useInMemoryPgpKeys(
      properties["GPG_KEY_ID"],
      properties["GPG_KEY"],
      properties["GPG_PASSWORD"]
    )
    sign(publishing.publications)
  }
}

operator fun Properties?.get(name: String): String? {
  var property = System.getenv(name)
  if (property.isNullOrEmpty()) {
    property = this?.getProperty(name)
  }
  return property
}