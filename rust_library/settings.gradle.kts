enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
  includeBuild("../third_party/uniffi-kotlin-multiplatform-bindings/build-logic")
  repositories {
    google {
      mavenContent {
        includeGroupByRegex(".*google.*")
        includeGroupByRegex(".*android.*")
      }
    }
    gradlePluginPortal()
    mavenCentral()
  }
}

dependencyResolutionManagement {
  repositories {
    google {
      mavenContent {
        includeGroupByRegex(".*google.*")
        includeGroupByRegex(".*android.*")
      }
    }
    mavenCentral()
  }
}

rootProject.name = "rust-library"
//include(":ziplib")
//include(":reverse_proxy")
include(":multipart")
//include(":biometrics")
