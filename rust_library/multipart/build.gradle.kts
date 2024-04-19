import io.gitlab.trixnity.gradle.CargoHost
import io.gitlab.trixnity.gradle.Variant
import io.gitlab.trixnity.gradle.cargo.dsl.android

plugins {
  id(libs.plugins.kotlinxMultiplatform.get().pluginId)
  id(libs.plugins.androidLibrary.get().pluginId)
  id(libs.plugins.kotlinxAtomicfu.get().pluginId) version libs.versions.kotlin
  id("io.gitlab.trixnity.uniffi.kotlin.multiplatform")
  id("io.gitlab.trixnity.cargo.kotlin.multiplatform")
}

kotlin {
  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
      }
    }
  }

  jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
  }

  mingwX64()

  if(CargoHost.Platform.MacOS.isCurrent) {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
    macosX64()
  }

  sourceSets.all {
    languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
  }
  jvm("desktop")
}

cargo {
//  builds.all {
//    variants.forEach {
//      println("QAQ ${it.rustTarget.rustTriple}")
//      it.profile = CargoProfile.Release
//    }
//  }
  packageDirectory = layout.projectDirectory.dir("uniffi")
  jvmVariant = Variant.Release
  nativeVariant = Variant.Release
  builds.android {
    variants.forEach {
      it.profile = CargoProfile.Release
    }
  }
//  android.defaultConfig {
//    ndk.abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//  }
}

uniffi {
  bindgenCratePath = rootProject.layout.projectDirectory.dir("../third_party/uniffi-kotlin-multiplatform-bindings/bindgen")
  generateFromUdl {
    udlFile = layout.projectDirectory.file("uniffi/src/multipart.udl")
    namespace = "multipart"
  }
}

android {
  namespace = "org.dweb_browser.multipart"
  compileSdk = libs.versions.compileSdkVersion.get().toInt()
  defaultConfig {
    minSdk = libs.versions.minSdkVersion.get().toInt()
    consumerProguardFiles("consumer-rules.pro")
    ndk {
      abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
    }
  }
}
