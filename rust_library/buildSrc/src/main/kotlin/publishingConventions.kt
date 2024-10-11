import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.publishingConventions() {
  val properties = project.localProperties()
  plugins.withType<MavenPublishPlugin>().configureEach {
    extensions.configure<PublishingExtension> {
      repositories {
        maven("https://maven.pkg.github.com/BioforestChain/dweb_browser_libs") {
          name = project.name
          description = project.description

          credentials {
            username = properties.getString("gpr.user")
            password = properties.getString("gpr.key")
          }
        }
      }
    }
  }

  plugins.withId("com.android.library") {
    extensions.configure<KotlinMultiplatformExtension> {
      androidTarget().publishLibraryVariants("release")
    }
  }
}