package dev.arunkumar.scabbard.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScabbardGradlePluginPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.tasks.register("greeting") { task ->
      task.doLast {
        println("Hello from plugin 'dev.arunkumar.scabbard.greeting'")
      }
    }
  }
}