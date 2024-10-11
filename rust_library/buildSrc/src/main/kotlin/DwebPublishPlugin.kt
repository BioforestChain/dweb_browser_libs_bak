import com.vanniktech.maven.publish.MavenPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project


class DwebPublishPlugin: Plugin<Project> {
  override fun apply(target: Project) = with(target) {
    plugins.apply(MavenPublishPlugin::class.java)

    target.publishingConventions()
  }
}