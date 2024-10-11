plugins {
  //trick: for the same plugin versions in all sub-modules
//    alias(libs.plugins.androidLibrary).apply(false)
//    alias(libs.plugins.kotlinxMultiplatform).apply(false)
}


tasks.create("DwebPublish") {
  doFirst {
    println("publish start")
  }

//  dependsOn()
  println("QAQ ${tasks.names}")

  doLast {
    println("publish end")
  }
}