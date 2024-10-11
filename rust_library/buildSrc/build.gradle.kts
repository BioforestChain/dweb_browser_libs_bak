plugins {
  `kotlin-dsl`
}

repositories {
  google {
    mavenContent {
      includeGroupByRegex(".*google.*")
      includeGroupByRegex(".*android.*")
    }
  }

  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  implementation(libs.android.gradle.plugin)
  implementation(libs.kotlin.gradle.plugin)
  implementation(libs.maven.publish.gradle.plugin)
}

gradlePlugin.plugins.create("dweb-publish") {
  id = "dweb-publish"
  implementationClass = "DwebPublishPlugin"
}

