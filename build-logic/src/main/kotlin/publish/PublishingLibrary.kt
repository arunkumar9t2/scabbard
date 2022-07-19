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

package publish

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.api.AndroidSourceDirectorySet
import gradle.ConfigurablePlugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.plugins.signing.SigningExtension
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaPlugin


public class PublishingLibrary : ConfigurablePlugin({
  apply(plugin = "maven-publish")
  apply(plugin = "signing")

  val isAndroid = project.plugins.hasPlugin("com.android.library")

  configureDokka()

  // Setup sources jar
  val sourceJarTask = registerSourceJarTask()
  // Documentation
  val javaDocsTask = registerJavaDocsTask()

  artifacts {
    add("archives", sourceJarTask)
    add("archives", javaDocsTask)
  }

  val website = findProperty("website").toString()
  val desc = if (description.isNullOrEmpty()) {
    findProperty("description").toString()
  } else description

  // Setup publishing
  afterEvaluate {
    configure<PublishingExtension> {
      publications {
        create<MavenPublication>("release") {
          groupId = findProperty("groupId").toString()
          artifactId = project.name
          version = project.version.toString()

          if (isAndroid) {
            from(components["release"])
          } else {
            from(components["java"])
          }

          artifact(sourceJarTask)
          artifact(javaDocsTask)

          pom {
            name.set(project.name)
            description.set(desc)
            url.set(website)

            licenses {
              license {
                name.set("Apache License, Version 2.0")
                url.set("https://raw.githubusercontent.com/arunkumar9t2/scabbard/main/LICENSE")
              }
            }

            developers {
              developer {
                id.set("arunkumar9t2")
                name.set("Arunkumar")
                email.set("hi@arunkumar.dev")
              }
            }

            scm {
              connection.set("${website}.git")
              developerConnection.set("${website}.git")
              url.set(website)
            }
          }
        }
      }
    }
  }

  configureSigning()
})

private fun Project.configureDokka() {
  apply<DokkaPlugin>()
}

private fun Project.registerJavaDocsTask(): TaskProvider<Jar> {
  val javaDocsTask = tasks.register<Jar>("javadocJar")
  val dokkaJavaDocTask = tasks.named<AbstractDokkaTask>("dokkaJavadoc")
  javaDocsTask.configure {
    archiveClassifier.set("javadoc")
    dependsOn(dokkaJavaDocTask)
    from(dokkaJavaDocTask.map { it.outputDirectory })
  }
  return javaDocsTask
}

private fun Project.registerSourceJarTask(): TaskProvider<Jar> {
  val sourcesJar = "sourcesJar"
  val isAndroid = project.plugins.hasPlugin("com.android.library")

  return tasks.register<Jar>(sourcesJar) {
    archiveClassifier.set("sources")
    if (isAndroid) {
      from(project.provider {
        extensions.getByType<BaseExtension>()
          .sourceSets
          .matching { it.name == "main" }
          .flatMap { it.java.srcDirs + (it.kotlin as AndroidSourceDirectorySet).srcDirs }
      })
    } else {
      extensions.findByType<SourceSetContainer>()
        ?.getByName("main")
        ?.allJava
        ?.srcDirs
        ?.let { from(it) }
    }
  }
}

private fun Project.configureSigning() {
  configure<SigningExtension> {
    useInMemoryPgpKeys(
      rootProject.extra[SIGNING_KEY_ID].toString(),
      rootProject.extra[SIGNING_KEY].toString(),
      rootProject.extra[SIGNING_PASSWORD].toString(),
    )
    sign(the<PublishingExtension>().publications)
  }
}
