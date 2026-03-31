/*
 * Copyright 2022 Arunkumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
  id("build-common")
  `java-gradle-plugin`
  alias(deps.plugins.binary.validator)
  alias(deps.plugins.gradle.publish)
  `kotlin-dsl`
}

group = property("groupId") as String
version = property("publishVersion") as String

repositories {
  mavenCentral()
  google()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation(deps.kotlin)
  compileOnly(deps.agp)

  testImplementation(deps.junit)
  testImplementation(deps.truth)
  testImplementation(gradleTestKit())
}

gradlePlugin {
  website = findProperty("website") as String?
  vcsUrl = findProperty("website") as String?
  plugins {
    create("scabbardPlugin") {
      id = property("scabbardGradlePlugin") as String
      displayName = "Scabbard - Dagger 2 Visualizer"
      description = "Gradle plugin to visualize Dagger 2 dependency graph"
      implementationClass = "dev.arunkumar.scabbard.gradle.ScabbardGradlePlugin"
      tags = listOf("dagger 2", "dependency injection", "visualize dependency graph")
    }
  }
}

sourceSets {
  create("functionalTest") {
    java.srcDirs("src/functionalTest/kotlin")
  }
  named("main") {
    java.srcDir("build/generated/kotlin")
  }
}

gradlePlugin.testSourceSets(sourceSets.named("functionalTest").get())
configurations.named("functionalTestImplementation") {
  extendsFrom(configurations.named("testImplementation").get())
}

tasks.register<Test>("functionalTest") {
  testClassesDirs = sourceSets.named("functionalTest").get().output.classesDirs
  classpath = sourceSets.named("functionalTest").get().runtimeClasspath
}

tasks.named("check") {
  dependsOn(tasks.named("functionalTest"))
}

val groupId = property("groupId") as String
val generatePluginVersion = tasks.register("generatePluginVersion") {
  val outputDir = file("build/generated/kotlin")
  inputs.property("version", version)
  outputs.dir(outputDir)
  doLast {
    val packageName = groupId + "." + project.name.replace("-", ".")
    val versionFile = file("${outputDir}/${packageName.replace(".", "/")}/Version.kt")
    versionFile.parentFile.mkdirs()
    versionFile.writeText(
      """// Generated file. Do not edit!
package $packageName
const val VERSION = "${project.version}"
"""
    )
  }
}

tasks.named("compileKotlin") {
  dependsOn(generatePluginVersion)
}

tasks.withType<Jar>().configureEach {
  dependsOn(generatePluginVersion)
}
