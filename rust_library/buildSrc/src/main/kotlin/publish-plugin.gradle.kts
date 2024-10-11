plugins {
  id("com.vanniktech.maven.publish")
}

afterEvaluate {
  val properties = project.localProperties()

  publishing {
    repositories {
      maven("https://maven.pkg.github.com/BioforestChain/dweb_browser_libs") {
        println("QAQ project.name=${project.name}")
        name = project.name
        description = project.description

        credentials {
          username = properties.getString("gpr.user")
          password = properties.getString("gpr.key")
        }
      }
    }
  }

  mavenPublishing {
    coordinates(
      groupId = "org.dweb_browser",
      artifactId = project.name,
      version = project.version.toString()
    )

    pom {
      name.set("Dweb KMP Library")
      description.set(project.description)
      inceptionYear.set("2024")
      url.set("https://github.com/BioforestChain/dweb_browser_libs")

      licenses {
        license {
          name.set("MIT")
          url.set("https://opensource.org/licenses/MIT")
        }
      }

      // Specify developers information
      developers {
        developer {
          id.set("BioforestChain")
          name.set("dweb")
          email.set("developer@bfchain.org")
        }
      }

      // Specify SCM information
      scm {
        url.set("https://github.com/BioforestChain/dweb_browser_libs")
      }
    }
  }

  tasks.register("${project.name}_publish") {
    println("QAQ xxx")
    exec {
      workingDir = projectDir
      commandLine("../gradlew", "publishAllPublicationsTo${project.name.uppercase()}Repository")
    }
  }
//  tasks.named("DwebPublish") {
//    println("QAQ 111")
//    dependsOn("${project.name}_publish")
//  }
}

