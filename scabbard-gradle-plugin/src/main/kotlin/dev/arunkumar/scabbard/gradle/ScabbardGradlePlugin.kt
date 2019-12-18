package dev.arunkumar.scabbard.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class ScabbardGradlePlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val scabbardExtension = project.extensions.create<ScabbardExtension>(SCABBARD)
    if (scabbardExtension.enabled) {
      project.dependencies.add("kapt", "dev.arunkumar:scabbard-processor:0.1.0")
    }
  }
}